document.addEventListener('DOMContentLoaded', function () {
  highlightActiveNav();
  setupMobileToggle();
  checkConnection();
  pageInit();
});

function highlightActiveNav() {
  const page = location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.nav-list a').forEach(a => {
    a.classList.toggle('active', a.getAttribute('href') === page);
  });
}

function setupMobileToggle() {
  const btn = document.getElementById('mobileToggle');
  if (btn) {
    btn.addEventListener('click', () => {
      document.querySelector('.sidebar').classList.toggle('open');
    });
  }
  document.addEventListener('click', (e) => {
    const sidebar = document.querySelector('.sidebar');
    if (window.innerWidth <= 768 && sidebar.classList.contains('open') &&
        !sidebar.contains(e.target) && e.target !== btn) {
      sidebar.classList.remove('open');
    }
  });
}

async function checkConnection() {
  const status = await getServerStatus();
  let badgeEl = document.getElementById('connStatus');
  if (!badgeEl) {
    const topBar = document.querySelector('.top-bar');
    if (topBar) {
      const div = topBar.querySelector('div');
      badgeEl = document.createElement('span');
      badgeEl.id = 'connStatus';
      badgeEl.style.cssText = 'font-size:12px;padding:4px 12px;border-radius:4px;margin-left:12px;white-space:nowrap;';
      div.appendChild(badgeEl);
    }
  }
  if (badgeEl) {
    if (status.online) {
      badgeEl.style.cssText += 'background:#dcfce7;color:#166534;font-size:12px;padding:4px 12px;border-radius:4px;margin-left:12px;';
      badgeEl.innerHTML = '<i class="bi bi-database"></i> Database Connected';
    } else {
      badgeEl.style.cssText += 'background:#fef3c7;color:#92400e;font-size:12px;padding:4px 12px;border-radius:4px;margin-left:12px;';
      badgeEl.innerHTML = '<i class="bi bi-cloud-off"></i> Offline (localStorage)';
    }
  }
}

function showAlert(msg, type = 'success') {
  const el = document.getElementById('alertMessage');
  if (!el) return;
  el.className = `alert alert-${type} show`;
  el.innerHTML = `<i class="bi ${type === 'success' ? 'bi-check-circle' : 'bi-exclamation-circle'}"></i> ${msg}`;
  el.style.display = 'flex';
  setTimeout(() => { el.style.display = 'none'; el.classList.remove('show'); }, 3000);
}

function showLoading(el) {
  el.innerHTML = '<div class="loading"><i class="bi bi-arrow-clockwise"></i>Loading...</div>';
}

function showEmpty(el, msg = 'No records found') {
  el.innerHTML = `<div class="empty-state"><i class="bi bi-inbox"></i>${msg}</div>`;
}

function populateSelect(selectId, items, valueKey, textKey, placeholder = '-- Select --') {
  const sel = document.getElementById(selectId);
  if (!sel) return;
  sel.innerHTML = `<option value="">${placeholder}</option>`;
  items.forEach(item => {
    const opt = document.createElement('option');
    opt.value = item[valueKey];
    opt.textContent = item[textKey];
    sel.appendChild(opt);
  });
}

function formatDate(d) {
  if (!d) return '';
  const date = new Date(d);
  if (isNaN(date)) return d;
  return date.toLocaleDateString('en-GB');
}

function pageInit() {
  const page = location.pathname.split('/').pop();
  if (page === 'index.html' || page === '' || page === 'HospitalManagementSystem4/') {
    loadDashboard();
  } else if (page === 'patients.html') {
    loadPatients();
  } else if (page === 'inpatients.html') {
    loadInpatients();
  } else if (page === 'doctors.html') {
    loadDoctors();
  } else if (page === 'appointments.html') {
    loadAppointments();
  } else if (page === 'allocations.html') {
    if (typeof loadAllocations === 'function') loadAllocations();
  } else if (page === 'payments.html') {
    if (typeof loadPayments === 'function') loadPayments();
  }
}
