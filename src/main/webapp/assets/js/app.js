'use strict';

// La ruta relativa funciona tanto en la raíz local como bajo el contexto de Tomcat.
const contextPath = new URL('.', window.location.href).pathname.replace(/\/$/, '');
const apiBase = `${contextPath}/api`;
const state = {
    view: 'inicio',
    data: { personajes: [], divisiones: [], zanpakutos: [], habilidades: [] },
    filters: {},
    feedbackTimer: null
};

const content = document.querySelector('#app-content');
const feedback = document.querySelector('#feedback');
const editorDialog = document.querySelector('#editor-dialog');
const editorContent = document.querySelector('#editor-content');
const detailDialog = document.querySelector('#detail-dialog');
const detailContent = document.querySelector('#detail-content');

const resources = {
    personajes: { singular: 'personaje', plural: 'personajes', title: 'Personajes' },
    divisiones: { singular: 'división', plural: 'divisiones', title: 'Divisiones del Gotei 13' },
    zanpakutos: { singular: 'zanpakuto', plural: 'zanpakutos', title: 'Zanpakutos' },
    habilidades: { singular: 'habilidad', plural: 'habilidades', title: 'Habilidades' }
};

async function request(path, options = {}) {
    const config = { ...options, headers: { Accept: 'application/json', ...options.headers } };
    if (options.body) config.headers['Content-Type'] = 'application/json';

    let response;
    try {
        response = await fetch(`${apiBase}${path}`, config);
    } catch (error) {
        throw new Error('No se pudo conectar con la API. Comprueba Tomcat y la conexión con MySQL.');
    }

    let result = null;
    try {
        result = await response.json();
    } catch (error) {
        if (response.ok) throw new Error('La respuesta del servidor no contiene JSON válido.');
    }
    if (!response.ok) throw new Error(result?.error || `La operación ha fallado (HTTP ${response.status}).`);
    return result;
}

async function loadData() {
    setApiStatus('Conectando con el archivo…', 'pending');
    try {
        const [personajes, divisiones, zanpakutos, habilidades] = await Promise.all([
            request('/personajes'), request('/divisiones'), request('/zanpakutos'), request('/habilidades')
        ]);
        state.data = { personajes, divisiones, zanpakutos, habilidades };
        setApiStatus('API conectada · datos en vivo', 'online');
        renderView();
    } catch (error) {
        setApiStatus('API no disponible', 'offline');
        content.innerHTML = `<section class="empty-state"><strong>No se pudo abrir el archivo</strong><p>${escapeHtml(error.message)} Cuando la API vuelva a estar disponible, recarga la página para consultar los datos reales.</p></section>`;
        showFeedback(error.message, 'error');
    }
}

function setApiStatus(message, status) {
    const indicator = document.querySelector('.api-indicator');
    document.querySelector('#api-status').textContent = message;
    indicator.classList.toggle('is-online', status === 'online');
    indicator.classList.toggle('is-offline', status === 'offline');
}

function showFeedback(message, type = 'success') {
    window.clearTimeout(state.feedbackTimer);
    feedback.textContent = message;
    feedback.hidden = false;
    feedback.classList.toggle('is-error', type === 'error');
    state.feedbackTimer = window.setTimeout(() => { feedback.hidden = true; }, 4800);
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, character => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[character]);
}

function fullName(personaje) {
    return [personaje.nombre, personaje.apellido].filter(Boolean).join(' ');
}

function findDivision(id) {
    return state.data.divisiones.find(division => Number(division.id) === Number(id));
}

function findPersonaje(id) {
    return state.data.personajes.find(personaje => Number(personaje.id) === Number(id));
}

function renderView() {
    const title = state.view === 'inicio' ? 'Inicio' : resources[state.view].title;
    document.querySelector('#page-title').textContent = title;
    document.querySelectorAll('.nav-link[data-view]').forEach(link => {
        link.classList.toggle('is-active', link.dataset.view === state.view);
    });

    if (state.view === 'inicio') renderHome();
    else renderResourcePage(state.view);
}

function renderHome() {
    const { personajes, divisiones, zanpakutos, habilidades } = state.data;
    content.innerHTML = `
        <section class="hero">
            <div class="hero-copy">
                <p class="eyebrow">Crónicas de la Sociedad de Almas</p>
                <h1>BLEACH<br><span>SOUL SOCIETY</span></h1>
                <p class="hero-description">Un archivo para explorar las almas, sus escuadrones y las armas que despiertan su verdadero poder. Descubre el universo de Bleach a través de sus personajes y legados.</p>
                <div class="hero-actions">
                    <button class="button button-primary" type="button" data-view="personajes">Explorar personajes <span aria-hidden="true">→</span></button>
                    <button class="button button-secondary" type="button" data-view="divisiones">Conocer el Gotei 13</button>
                </div>
            </div>
            <div class="hero-emblem" aria-hidden="true">卍</div>
        </section>
        <div class="stats-grid" aria-label="Registros consultados en la base de datos">
            ${statCard('Personajes', personajes.length, '✦')}
            ${statCard('Divisiones', divisiones.length, '⌘')}
            ${statCard('Zanpakutos', zanpakutos.length, '〽')}
            ${statCard('Habilidades', habilidades.length, '✧')}
        </div>
        <div class="home-bottom">
            <article class="editorial-card">
                <p class="eyebrow">El archivo</p>
                <h3>Historias que trascienden mundos</h3>
                <p>Desde las calles de Karakura hasta los trece escuadrones, cada registro reúne personajes y poderes vinculados a la historia de Bleach. Consulta, organiza y amplía este archivo conectado a la base de datos.</p>
            </article>
            <article class="quote-card">
                <p class="eyebrow">Tu recorrido comienza aquí</p>
                <h3>Encuentra tu próximo capítulo</h3>
                <p>Busca un nombre, explora una división o descubre el vínculo entre un shinigami y su zanpakuto.</p>
            </article>
        </div>`;
}

function statCard(label, value, icon) {
    return `<article class="stat-card"><span class="stat-label">${icon} &nbsp;${label} registrados</span><strong class="stat-value accent">${value}</strong></article>`;
}

function renderResourcePage(kind) {
    const resource = resources[kind];
    const filters = state.filters[kind] || {};
    const records = filterRecords(kind, filters);

    content.innerHTML = `
        <section class="page-heading">
            <div><p class="eyebrow">Archivo de la Sociedad de Almas</p><h1>${resource.title}</h1><p>${pageDescription(kind)}</p></div>
            <button class="button button-primary" type="button" data-action="create" data-kind="${kind}"><span aria-hidden="true">＋</span> Añadir ${resource.singular}</button>
        </section>
        <form class="filter-panel" id="filters-form">
            ${filterControls(kind, filters)}
            <button class="button button-secondary" type="submit">Buscar</button>
            <button class="button button-secondary" type="button" data-action="clear-filters">Limpiar</button>
        </form>
        <p class="result-count">${records.length} ${records.length === 1 ? resource.singular : resource.plural} en el archivo</p>
        <div class="card-grid ${kind === 'divisiones' || kind === 'zanpakutos' ? 'two-columns' : ''}">${renderCards(kind, records)}</div>`;
    wirePortraits(content);

    const form = document.querySelector('#filters-form');
    form.addEventListener('submit', event => {
        event.preventDefault();
        state.filters[kind] = Object.fromEntries(new FormData(form).entries());
        renderResourcePage(kind);
    });
}

function pageDescription(kind) {
    return {
        personajes: 'Explora sus historias, rangos y vínculos con los escuadrones.',
        divisiones: 'Conoce los trece escuadrones y las almas que forman parte de ellos.',
        zanpakutos: 'Cada espada refleja el poder y el espíritu de su portador.',
        habilidades: 'Técnicas y poderes registrados en el archivo espiritual.'
    }[kind];
}

function filterControls(kind, filters) {
    const name = `<div class="field"><label for="filter-name">Buscar por nombre</label><input id="filter-name" name="nombre" type="search" maxlength="100" placeholder="Escribe un nombre…" value="${escapeHtml(filters.nombre || '')}"></div>`;
    if (kind === 'personajes') {
        const species = [...new Set(state.data.personajes.map(personaje => personaje.especie).filter(Boolean))].sort();
        return `${name}<div class="field"><label for="filter-species">Raza</label><select id="filter-species" name="especie"><option value="">Todas las razas</option>${species.map(value => `<option value="${escapeHtml(value)}" ${filters.especie === value ? 'selected' : ''}>${escapeHtml(value)}</option>`).join('')}</select></div><div class="field"><label for="filter-division">División</label><select id="filter-division" name="divisionId"><option value="">Todas las divisiones</option>${state.data.divisiones.map(item => `<option value="${item.id}" ${String(filters.divisionId || '') === String(item.id) ? 'selected' : ''}>${escapeHtml(item.nombre)}</option>`).join('')}</select></div>`;
    }
    if (kind === 'zanpakutos') return `${name}<div class="field"><label for="filter-owner">Portador</label><select id="filter-owner" name="personajeId"><option value="">Todos los portadores</option>${state.data.personajes.map(item => `<option value="${item.id}" ${String(filters.personajeId || '') === String(item.id) ? 'selected' : ''}>${escapeHtml(fullName(item))}</option>`).join('')}</select></div>`;
    if (kind === 'habilidades') {
        const types = [...new Set(state.data.habilidades.map(item => item.tipo).filter(Boolean))].sort();
        return `${name}<div class="field"><label for="filter-type">Tipo</label><select id="filter-type" name="tipo"><option value="">Todos los tipos</option>${types.map(value => `<option value="${escapeHtml(value)}" ${filters.tipo === value ? 'selected' : ''}>${escapeHtml(value)}</option>`).join('')}</select></div>`;
    }
    return name;
}

function filterRecords(kind, filters) {
    let records = [...state.data[kind]];
    const query = (filters.nombre || '').trim().toLocaleLowerCase('es');
    if (query) records = records.filter(record => searchableText(kind, record).toLocaleLowerCase('es').includes(query));
    if (kind === 'personajes') {
        if (filters.especie) records = records.filter(item => item.especie === filters.especie);
        if (filters.divisionId) records = records.filter(item => String(item.divisionId || '') === filters.divisionId);
    }
    if (kind === 'zanpakutos' && filters.personajeId) records = records.filter(item => String(item.personajeId) === filters.personajeId);
    if (kind === 'habilidades' && filters.tipo) records = records.filter(item => item.tipo === filters.tipo);
    return records;
}

function searchableText(kind, record) {
    if (kind === 'personajes') return `${fullName(record)} ${record.especie || ''} ${record.rango || ''}`;
    if (kind === 'zanpakutos') return `${record.nombre} ${record.shikai || ''} ${record.bankai || ''}`;
    return `${record.nombre} ${record.descripcion || ''} ${record.tipo || ''}`;
}

function renderCards(kind, records) {
    if (!records.length) return '<div class="empty-state"><strong>No hay registros para mostrar</strong><p>Prueba con otros filtros o añade un nuevo registro al archivo.</p></div>';
    return records.map(record => {
        if (kind === 'personajes') return characterCard(record);
        if (kind === 'divisiones') return divisionCard(record);
        if (kind === 'zanpakutos') return zanpakutoCard(record);
        return habilidadCard(record);
    }).join('');
}

function recordActions(kind, record) {
    return `<div class="card-actions"><button class="button button-secondary button-small" type="button" data-action="detail" data-kind="${kind}" data-id="${record.id}">Detalles</button><button class="button button-secondary button-small button-icon" type="button" aria-label="Editar" title="Editar" data-action="edit" data-kind="${kind}" data-id="${record.id}">✎</button><button class="button button-danger button-small button-icon" type="button" aria-label="Eliminar" title="Eliminar" data-action="delete" data-kind="${kind}" data-id="${record.id}">×</button></div>`;
}

function characterCard(personaje) {
    const division = findDivision(personaje.divisionId);
    return `<article class="archive-card character-card">
        <div class="character-top">${portrait(personaje)}<div class="character-name"><h3>${escapeHtml(fullName(personaje))}</h3><p>${escapeHtml(personaje.rango || 'Sin rango registrado')}</p></div></div>
        <div class="card-description"><span class="tag tag-accent">${escapeHtml(personaje.especie)}</span><span class="tag">${escapeHtml(division?.nombre || 'Sin división')}</span></div>
        <div class="card-footer"><span class="tag">Registro #${personaje.id}</span>${recordActions('personajes', personaje)}</div>
    </article>`;
}

function portrait(personaje) {
    const initials = [personaje.nombre, personaje.apellido].filter(Boolean).map(part => part.charAt(0)).join('').slice(0, 2);
    return `<div class="portrait"><img src="${contextPath}/assets/images/personajes/${Number(personaje.id)}.webp" alt="Retrato local de ${escapeHtml(fullName(personaje))}"><span class="portrait-fallback" aria-hidden="true">${escapeHtml(initials || '魂')}</span></div>`;
}

function wirePortraits(root) {
    root.querySelectorAll('.portrait img').forEach(image => {
        const fallback = image.nextElementSibling;
        image.addEventListener('load', () => { fallback.hidden = true; }, { once: true });
        image.addEventListener('error', () => { image.hidden = true; fallback.hidden = false; }, { once: true });
        if (image.complete) {
            if (image.naturalWidth) fallback.hidden = true;
            else { image.hidden = true; fallback.hidden = false; }
        }
    });
}

function divisionCard(division) {
    const count = state.data.personajes.filter(personaje => Number(personaje.divisionId) === Number(division.id)).length;
    return `<article class="archive-card entity-card"><div class="entity-card-top"><div><p class="eyebrow">Escuadrón ${division.id}</p><h3>${escapeHtml(division.nombre)}</h3></div><span class="entity-symbol" aria-hidden="true">⌘</span></div><p>${escapeHtml(division.descripcion || 'Sin descripción registrada.')}</p><div class="card-footer"><span class="tag">${count} ${count === 1 ? 'personaje' : 'personajes'}</span>${recordActions('divisiones', division)}</div></article>`;
}

function zanpakutoCard(zanpakuto) {
    const owner = findPersonaje(zanpakuto.personajeId);
    return `<article class="archive-card entity-card"><div class="entity-card-top"><div><p class="eyebrow">Zanpakuto · #${zanpakuto.id}</p><h3>${escapeHtml(zanpakuto.nombre)}</h3></div><span class="entity-symbol" aria-hidden="true">〽</span></div><p>Portador: ${escapeHtml(owner ? fullName(owner) : `Registro #${zanpakuto.personajeId}`)}<br>Shikai: ${escapeHtml(zanpakuto.shikai || 'No registrado')}<br>Bankai: ${escapeHtml(zanpakuto.bankai || 'No registrado')}</p><div class="card-footer"><span class="tag tag-accent">Arma espiritual</span>${recordActions('zanpakutos', zanpakuto)}</div></article>`;
}

function habilidadCard(habilidad) {
    return `<article class="archive-card entity-card"><div class="entity-card-top"><div><p class="eyebrow">Técnica · #${habilidad.id}</p><h3>${escapeHtml(habilidad.nombre)}</h3></div><span class="entity-symbol" aria-hidden="true">✧</span></div><p>${escapeHtml(habilidad.descripcion || 'Sin descripción registrada.')}</p><div class="card-footer"><span class="tag tag-accent">${escapeHtml(habilidad.tipo)}</span>${recordActions('habilidades', habilidad)}</div></article>`;
}

function getRecord(kind, id) {
    return state.data[kind].find(record => Number(record.id) === Number(id));
}

function openEditor(kind, record = null) {
    const heading = record ? `Editar ${resources[kind].singular}` : `Añadir ${resources[kind].singular}`;
    editorContent.innerHTML = `<div class="modal-inner"><div class="modal-header"><div><p class="eyebrow">Archivo espiritual</p><h2 id="editor-title">${heading}</h2><p>Los cambios se guardarán en la base de datos.</p></div><button class="modal-close" type="button" data-action="close-editor" aria-label="Cerrar">×</button></div><form id="record-form" data-kind="${kind}" data-id="${record?.id || ''}"><div class="form-grid">${editorFields(kind, record)}</div><div class="form-actions"><button class="button button-secondary" type="button" data-action="close-editor">Cancelar</button><button class="button button-primary" type="submit">Guardar registro</button></div></form></div>`;
    editorDialog.showModal();
    document.querySelector('#record-form').addEventListener('submit', saveRecord);
}

function editorFields(kind, record) {
    const text = (name, label, max, required = false, value = '', full = false) => `<div class="field ${full ? 'field-full' : ''}"><label for="field-${name}">${label}${required ? ' *' : ''}</label><input id="field-${name}" name="${name}" type="text" maxlength="${max}" value="${escapeHtml(value)}" ${required ? 'required' : ''}></div>`;
    const textarea = (name, label, max, value = '') => `<div class="field field-full"><label for="field-${name}">${label}</label><textarea id="field-${name}" name="${name}" maxlength="${max}">${escapeHtml(value || '')}</textarea></div>`;
    if (kind === 'personajes') {
        return `${text('nombre', 'Nombre', 100, true, record?.nombre)}${text('apellido', 'Apellido', 100, false, record?.apellido)}${text('especie', 'Raza / especie', 60, true, record?.especie)}${text('rango', 'Rango', 100, false, record?.rango)}<div class="field field-full"><label for="field-divisionId">División</label><select id="field-divisionId" name="divisionId"><option value="">Sin división</option>${state.data.divisiones.map(item => `<option value="${item.id}" ${Number(record?.divisionId) === Number(item.id) ? 'selected' : ''}>${escapeHtml(item.nombre)}</option>`).join('')}</select></div>`;
    }
    if (kind === 'divisiones') return `${text('nombre', 'Nombre', 100, true, record?.nombre, true)}${textarea('descripcion', 'Descripción', 500, record?.descripcion)}`;
    if (kind === 'zanpakutos') return `<div class="field field-full"><label for="field-personajeId">Portador *</label><select id="field-personajeId" name="personajeId" required><option value="">Selecciona un personaje</option>${state.data.personajes.map(item => `<option value="${item.id}" ${Number(record?.personajeId) === Number(item.id) ? 'selected' : ''}>${escapeHtml(fullName(item))}</option>`).join('')}</select></div>${text('nombre', 'Nombre', 100, true, record?.nombre, true)}${textarea('shikai', 'Shikai', 500, record?.shikai)}${textarea('bankai', 'Bankai', 500, record?.bankai)}`;
    return `${text('nombre', 'Nombre', 100, true, record?.nombre)}${text('tipo', 'Tipo', 50, true, record?.tipo)}${textarea('descripcion', 'Descripción', 500, record?.descripcion)}`;
}

async function saveRecord(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const kind = form.dataset.kind;
    const id = form.dataset.id;
    const values = Object.fromEntries(new FormData(form).entries());
    Object.keys(values).forEach(key => { values[key] = values[key].trim() || null; });
    if ('divisionId' in values || 'personajeId' in values) {
        const idField = 'divisionId' in values ? 'divisionId' : 'personajeId';
        values[idField] = values[idField] === null ? null : Number(values[idField]);
    }
    const button = form.querySelector('[type="submit"]');
    button.disabled = true;
    button.textContent = 'Guardando…';
    try {
        await request(`/${kind}${id ? `/${id}` : ''}`, { method: id ? 'PUT' : 'POST', body: JSON.stringify(values) });
        editorDialog.close();
        await refreshData();
        showFeedback(`${capitalize(resources[kind].singular)} ${id ? 'actualizado' : 'añadido'} correctamente.`);
    } catch (error) {
        button.disabled = false;
        button.textContent = 'Guardar registro';
        showFeedback(error.message, 'error');
    }
}

function capitalize(value) {
    return value.charAt(0).toLocaleUpperCase('es') + value.slice(1);
}

async function deleteRecord(kind, id) {
    const record = getRecord(kind, id);
    const label = record?.nombre ? `«${record.nombre}»` : `#${id}`;
    if (!window.confirm(`¿Seguro que quieres eliminar ${resources[kind].singular} ${label}? Esta acción no se puede deshacer.`)) return;
    try {
        await request(`/${kind}/${id}`, { method: 'DELETE' });
        if (detailDialog.open) detailDialog.close();
        await refreshData();
        showFeedback(`${capitalize(resources[kind].singular)} eliminado correctamente.`);
    } catch (error) {
        showFeedback(error.message, 'error');
    }
}

async function refreshData() {
    const [personajes, divisiones, zanpakutos, habilidades] = await Promise.all([
        request('/personajes'), request('/divisiones'), request('/zanpakutos'), request('/habilidades')
    ]);
    state.data = { personajes, divisiones, zanpakutos, habilidades };
    renderView();
}

async function openDetail(kind, id) {
    const record = getRecord(kind, id);
    if (!record) return;
    if (kind === 'personajes') {
        await openCharacterDetail(record);
        return;
    }
    const details = detailFields(kind, record);
    detailContent.innerHTML = `<div class="modal-inner"><div class="modal-header"><div><p class="eyebrow">${resources[kind].singular} · #${record.id}</p><h2 id="detail-title">${escapeHtml(record.nombre)}</h2></div><button class="modal-close" type="button" data-action="close-detail" aria-label="Cerrar">×</button></div><div class="detail-grid">${details.map(([label, value]) => `<div class="detail-item"><span>${escapeHtml(label)}</span><strong>${escapeHtml(value ?? 'No registrado')}</strong></div>`).join('')}</div><div class="detail-actions"><div><button class="button button-secondary" type="button" data-action="edit" data-kind="${kind}" data-id="${record.id}">Editar</button><button class="button button-danger" type="button" data-action="delete" data-kind="${kind}" data-id="${record.id}">Eliminar</button></div><button class="button button-secondary" type="button" data-action="close-detail">Cerrar</button></div></div>`;
    detailDialog.showModal();
}

function detailFields(kind, record) {
    if (kind === 'divisiones') return [['Descripción', record.descripcion], ['Personajes', state.data.personajes.filter(personaje => Number(personaje.divisionId) === Number(record.id)).length]];
    if (kind === 'zanpakutos') return [['Portador', fullName(findPersonaje(record.personajeId) || {})], ['Shikai', record.shikai], ['Bankai', record.bankai]];
    return [['Tipo', record.tipo], ['Descripción', record.descripcion]];
}

async function openCharacterDetail(personaje) {
    detailContent.innerHTML = '<div class="loading-panel"><span class="spinner" aria-hidden="true"></span><span>Cargando ficha…</span></div>';
    if (!detailDialog.open) detailDialog.showModal();
    try {
        const [habilidades, zanpakutos] = await Promise.all([
            request(`/personajes/${personaje.id}/habilidades`),
            request(`/zanpakutos?personajeId=${encodeURIComponent(personaje.id)}`)
        ]);
        const division = findDivision(personaje.divisionId);
        const assigned = new Set(habilidades.map(habilidad => Number(habilidad.id)));
        const available = state.data.habilidades.filter(habilidad => !assigned.has(Number(habilidad.id)));
        detailContent.innerHTML = `<div class="modal-inner">
            <div class="modal-header"><div><p class="eyebrow">Expediente · personaje #${personaje.id}</p><h2 id="detail-title">Ficha espiritual</h2></div><button class="modal-close" type="button" data-action="close-detail" aria-label="Cerrar">×</button></div>
            <div class="detail-hero">${portrait(personaje)}<div><h2>${escapeHtml(fullName(personaje))}</h2><p>${escapeHtml(personaje.rango || 'Sin rango')} · ${escapeHtml(personaje.especie)}</p></div></div>
            <div class="detail-grid"><div class="detail-item"><span>División</span><strong>${escapeHtml(division?.nombre || 'Sin división')}</strong></div><div class="detail-item"><span>Registro</span><strong>#${personaje.id}</strong></div></div>
            <section class="detail-section"><h3>Zanpakuto${zanpakutos.length === 1 ? '' : 's'}</h3>${zanpakutos.length ? `<div class="detail-list">${zanpakutos.map(item => `<span class="tag tag-accent">${escapeHtml(item.nombre)}</span>`).join('')}</div>` : '<p class="result-count">No hay zanpakutos asociados.</p>'}</section>
            <section class="detail-section"><h3>Habilidades asignadas</h3>${habilidades.length ? `<div class="detail-list">${habilidades.map(habilidad => `<span class="skill-chip">${escapeHtml(habilidad.nombre)}<button type="button" data-action="remove-skill" data-character="${personaje.id}" data-skill="${habilidad.id}" aria-label="Retirar ${escapeHtml(habilidad.nombre)}" title="Retirar habilidad">×</button></span>`).join('')}</div>` : '<p class="result-count">Todavía no tiene habilidades asignadas.</p>'}
                <form class="assignment-form" id="assignment-form" data-character="${personaje.id}"><select name="habilidadId" aria-label="Habilidad para asignar" ${available.length ? 'required' : 'disabled'}><option value="">${available.length ? 'Selecciona una habilidad' : 'No hay más habilidades disponibles'}</option>${available.map(item => `<option value="${item.id}">${escapeHtml(item.nombre)} · ${escapeHtml(item.tipo)}</option>`).join('')}</select><button class="button button-primary button-small" type="submit" ${available.length ? '' : 'disabled'}>Asignar habilidad</button></form>
            </section>
            <div class="detail-actions"><div><button class="button button-secondary" type="button" data-action="edit" data-kind="personajes" data-id="${personaje.id}">Editar personaje</button><button class="button button-danger" type="button" data-action="delete" data-kind="personajes" data-id="${personaje.id}">Eliminar</button></div><button class="button button-secondary" type="button" data-action="close-detail">Cerrar</button></div>
        </div>`;
        wirePortraits(detailContent);
        document.querySelector('#assignment-form').addEventListener('submit', assignSkill);
    } catch (error) {
        detailContent.innerHTML = `<div class="modal-inner"><div class="modal-header"><h2 id="detail-title">No se pudo cargar la ficha</h2><button class="modal-close" type="button" data-action="close-detail" aria-label="Cerrar">×</button></div><p>${escapeHtml(error.message)}</p></div>`;
    }
}

async function assignSkill(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const characterId = form.dataset.character;
    const skillId = Number(new FormData(form).get('habilidadId'));
    if (!skillId) return;
    const button = form.querySelector('button');
    button.disabled = true;
    try {
        await request(`/personajes/${characterId}/habilidades`, { method: 'POST', body: JSON.stringify({ habilidadId: skillId }) });
        await openCharacterDetail(findPersonaje(characterId));
        showFeedback('Habilidad asignada correctamente.');
    } catch (error) {
        button.disabled = false;
        showFeedback(error.message, 'error');
    }
}

async function removeSkill(characterId, skillId) {
    const skill = state.data.habilidades.find(item => Number(item.id) === Number(skillId));
    if (!window.confirm(`¿Retirar ${skill?.nombre || 'esta habilidad'} del personaje?`)) return;
    try {
        await request(`/personajes/${characterId}/habilidades/${skillId}`, { method: 'DELETE' });
        await openCharacterDetail(findPersonaje(characterId));
        showFeedback('Habilidad retirada correctamente.');
    } catch (error) {
        showFeedback(error.message, 'error');
    }
}

document.addEventListener('click', event => {
    const viewLink = event.target.closest('[data-view]');
    if (viewLink) {
        event.preventDefault();
        state.view = viewLink.dataset.view;
        renderView();
        return;
    }

    const action = event.target.closest('[data-action]');
    if (!action) return;
    const { action: name, kind, id } = action.dataset;
    if (name === 'create') openEditor(kind);
    if (name === 'edit') {
        const record = getRecord(kind, id);
        if (record) {
            if (detailDialog.open) detailDialog.close();
            openEditor(kind, record);
        }
    }
    if (name === 'detail') openDetail(kind, id);
    if (name === 'delete') deleteRecord(kind, id);
    if (name === 'close-editor') editorDialog.close();
    if (name === 'close-detail') detailDialog.close();
    if (name === 'clear-filters') {
        state.filters[state.view] = {};
        renderResourcePage(state.view);
    }
    if (name === 'remove-skill') removeSkill(action.dataset.character, action.dataset.skill);
});

editorDialog.addEventListener('click', event => {
    if (event.target === editorDialog) editorDialog.close();
});
detailDialog.addEventListener('click', event => {
    if (event.target === detailDialog) detailDialog.close();
});

loadData();