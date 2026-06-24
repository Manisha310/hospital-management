const API_BASE = 'http://localhost:8080/api';
let useLocal = false;
let serverChecked = false;

async function getServerStatus() {
  if (serverChecked) return { online: !useLocal, local: useLocal };
  try {
    const res = await fetch(`${API_BASE}/dashboard/stats`, { method: 'GET', headers: { 'Content-Type': 'application/json' } });
    if (res.ok) {
      serverChecked = true;
      useLocal = false;
      return { online: true, local: false };
    }
  } catch (e) {}
  serverChecked = true;
  useLocal = true;
  return { online: false, local: true };
}

async function apiRequest(method, endpoint, data = null) {
  if (useLocal) {
    return localRequest(method, endpoint, data);
  }
  const options = {
    method,
    headers: { 'Content-Type': 'application/json' },
  };
  if (data) options.body = JSON.stringify(data);
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, options);
    if (!res.ok) {
      const err = await res.text();
      throw new Error(err || `Request failed: ${res.status}`);
    }
    const text = await res.text();
    return text ? JSON.parse(text) : null;
  } catch (e) {
    console.warn('API server unavailable, switching to local storage mode');
    useLocal = true;
    return localRequest(method, endpoint, data);
  }
}

/* LocalStorage fallback */
const STORE = 'hms_data';

function getStore() {
  const raw = localStorage.getItem(STORE);
  if (raw) return JSON.parse(raw);
  return seedData();
}

function seedData() {
  const data = {
    outpatients: [
      {patientId:'PT101',patientName:'Arun Kumar',phNo:9876543210,age:35,gender:'Male',specialisation:'Cardiology',specialist:'Cardiology',medicalHistory:'Diabetes'},
      {patientId:'PT102',patientName:'Priya Devi',phNo:9876543211,age:28,gender:'Female',specialisation:'Neurology',specialist:'Neurology',medicalHistory:'Migraine'},
    ],
    inpatients: [
      {patientId:'IP201',patientName:'Ravi Shankar',phoneNumber:9876543220,age:55,gender:'Male',specialist:'Cardiology',medicalHistory:'Heart condition',treatment:'Angioplasty',roomType:'AC',food:'YES',days:5},
    ],
    doctors: [
      {doctorId:'DR3',doctorName:'Dr. Priya Sharma',specialization:'Cardiology',availableDate:'2026-06-25',availableTime:'10:00'},
      {doctorId:'DR5',doctorName:'Dr. Rajesh Kumar',specialization:'Neurology',availableDate:'2026-06-24',availableTime:'14:00'},
    ],
    appointments: [
      {appointmentId:'APT101',patientId:'PT101',doctorId:'DR5',specialist:'Neurology',appointmentDate:'2026-06-25',appointmentTime:'10:00'},
    ],
    allocations: [
      {allocationId:'ALC1',patientId:'IP201',roomNumber:101,noOfDays:5},
    ],
    payments: [
      {paymentId:'PAY1',patientId:'PT101',patientName:'Arun Kumar',patientType:'OutPatient',paymentDate:'2026-06-24',modeOfPayment:'Cash',billAmount:1500,doctorFee:500,medicineFees:300,roomFees:0,registrationFees:100,totalBill:900},
    ],
  };
  saveStore(data);
  return data;
}

function saveStore(s) {
  localStorage.setItem(STORE, JSON.stringify(s));
}

function genId(prefix, list) {
  let max = 0;
  list.forEach(item => {
    const idField = item.patientId || item.doctorId || item.appointmentId || item.id || '';
    const num = parseInt(idField.replace(prefix, ''), 10);
    if (num > max) max = num;
  });
  return prefix + (max + 1);
}

function localRequest(method, endpoint, data) {
  const store = getStore();
  const parts = endpoint.split('/').filter(Boolean);
  const resource = parts[0]; // patients, doctors, appointments
  const sub = parts[1]; // out, in, or id
  const id = parts.length > 2 ? decodeURIComponent(parts[parts.length - 1]) : null;
  const isSearch = endpoint.includes('search');

  /* Dispatch */
  if (resource === 'dashboard' && method === 'GET') {
    return {
      outPatients: store.outpatients.length,
      inPatients: store.inpatients.length,
      doctors: store.doctors.length,
      appointments: store.appointments.length,
      allocations: (store.allocations || []).length,
      payments: (store.payments || []).length,
      recentAppointments: store.appointments.slice(-5).reverse(),
      doctorsList: store.doctors.slice(0, 5),
    };
  }

  if (resource === 'patients') {
    let list = sub === 'out' ? store.outpatients : store.inpatients;
    if (method === 'GET') {
      if (isSearch) {
        const q = new URLSearchParams(endpoint.split('?')[1]).get('q').toLowerCase();
        return list.filter(p => p.id.toLowerCase().includes(q) || p.patientName.toLowerCase().includes(q));
      }
      return list;
    }
    if (method === 'POST') {
      const item = { ...data, patientId: genId(sub === 'out' ? 'PT' : 'IP', list) };
      list.push(item);
      if (sub === 'out') store.outpatients = list; else store.inpatients = list;
      saveStore(store);
      return { message: 'Created', id: item.patientId };
    }
    if (method === 'PUT') {
      const idx = list.findIndex(p => (p.patientId || p.id) === id);
      if (idx >= 0) { list[idx] = { ...list[idx], ...data }; }
      if (sub === 'out') store.outpatients = list; else store.inpatients = list;
      saveStore(store);
      return { message: 'Updated' };
    }
    if (method === 'DELETE') {
      const idx = list.findIndex(p => (p.patientId || p.id) === id);
      if (idx >= 0) list.splice(idx, 1);
      if (sub === 'out') store.outpatients = list; else store.inpatients = list;
      saveStore(store);
      return { message: 'Deleted' };
    }
  }

  if (resource === 'doctors') {
    if (method === 'GET') return store.doctors;
    if (method === 'POST') {
      const item = { ...data, doctorId: genId('DR', store.doctors) };
      store.doctors.push(item);
      saveStore(store);
      return { message: 'Created', id: item.doctorId };
    }
    if (method === 'PUT') {
      const idx = store.doctors.findIndex(d => (d.doctorId || d.id) === id);
      if (idx >= 0) store.doctors[idx] = { ...store.doctors[idx], ...data };
      saveStore(store);
      return { message: 'Updated' };
    }
    if (method === 'DELETE') {
      const idx = store.doctors.findIndex(d => (d.doctorId || d.id) === id);
      if (idx >= 0) store.doctors.splice(idx, 1);
      saveStore(store);
      return { message: 'Deleted' };
    }
  }

  if (resource === 'appointments') {
    if (method === 'GET') return store.appointments;
    if (method === 'POST') {
      const item = { ...data, appointmentId: genId('APT', store.appointments) };
      store.appointments.push(item);
      saveStore(store);
      return { message: 'Created', id: item.appointmentId };
    }
    if (method === 'PUT') {
      const idx = store.appointments.findIndex(a => (a.appointmentId || a.id) === id);
      if (idx >= 0) store.appointments[idx] = { ...store.appointments[idx], ...data };
      saveStore(store);
      return { message: 'Updated' };
    }
    if (method === 'DELETE') {
      const idx = store.appointments.findIndex(a => (a.appointmentId || a.id) === id);
      if (idx >= 0) store.appointments.splice(idx, 1);
      saveStore(store);
      return { message: 'Deleted' };
    }
  }

  if (resource === 'allocations') {
    if (method === 'GET') return store.allocations;
    if (method === 'POST') {
      const item = { ...data, allocationId: genId('ALC', store.allocations) };
      store.allocations.push(item);
      saveStore(store);
      return { message: 'Created', id: item.allocationId };
    }
    if (method === 'PUT') {
      const idx = store.allocations.findIndex(a => (a.allocationId || a.id) === id);
      if (idx >= 0) store.allocations[idx] = { ...store.allocations[idx], ...data };
      saveStore(store);
      return { message: 'Updated' };
    }
    if (method === 'DELETE') {
      const idx = store.allocations.findIndex(a => (a.allocationId || a.id) === id);
      if (idx >= 0) store.allocations.splice(idx, 1);
      saveStore(store);
      return { message: 'Deleted' };
    }
  }

  if (resource === 'payments') {
    if (method === 'GET') return store.payments;
    if (method === 'POST') {
      const item = { ...data, paymentId: genId('PAY', store.payments) };
      store.payments.push(item);
      saveStore(store);
      return { message: 'Created', id: item.paymentId };
    }
    if (method === 'PUT') {
      const idx = store.payments.findIndex(p => (p.paymentId || p.id) === id);
      if (idx >= 0) store.payments[idx] = { ...store.payments[idx], ...data };
      saveStore(store);
      return { message: 'Updated' };
    }
    if (method === 'DELETE') {
      const idx = store.payments.findIndex(p => (p.paymentId || p.id) === id);
      if (idx >= 0) store.payments.splice(idx, 1);
      saveStore(store);
      return { message: 'Deleted' };
    }
  }

  throw new Error('Unknown endpoint: ' + endpoint);
}

const api = {
  getDashboardStats: () => apiRequest('GET', '/dashboard/stats'),
  getOutpatients: () => apiRequest('GET', '/patients/out'),
  addOutpatient: (data) => apiRequest('POST', '/patients/out', data),
  updateOutpatient: (id, data) => apiRequest('PUT', `/patients/out/${id}`, data),
  deleteOutpatient: (id) => apiRequest('DELETE', `/patients/out/${id}`),
  searchOutpatients: (query) => apiRequest('GET', `/patients/out/search?q=${encodeURIComponent(query)}`),
  getInpatients: () => apiRequest('GET', '/patients/in'),
  addInpatient: (data) => apiRequest('POST', '/patients/in', data),
  updateInpatient: (id, data) => apiRequest('PUT', `/patients/in/${id}`, data),
  deleteInpatient: (id) => apiRequest('DELETE', `/patients/in/${id}`),
  getDoctors: () => apiRequest('GET', '/doctors'),
  addDoctor: (data) => apiRequest('POST', '/doctors', data),
  updateDoctor: (id, data) => apiRequest('PUT', `/doctors/${id}`, data),
  deleteDoctor: (id) => apiRequest('DELETE', `/doctors/${id}`),
  getAppointments: () => apiRequest('GET', '/appointments'),
  addAppointment: (data) => apiRequest('POST', '/appointments', data),
  updateAppointment: (id, data) => apiRequest('PUT', `/appointments/${id}`, data),
  deleteAppointment: (id) => apiRequest('DELETE', `/appointments/${id}`),
  getAllocations: () => apiRequest('GET', '/allocations'),
  addAllocation: (data) => apiRequest('POST', '/allocations', data),
  updateAllocation: (id, data) => apiRequest('PUT', `/allocations/${id}`, data),
  deleteAllocation: (id) => apiRequest('DELETE', `/allocations/${id}`),
  getPayments: () => apiRequest('GET', '/payments'),
  addPayment: (data) => apiRequest('POST', '/payments', data),
  updatePayment: (id, data) => apiRequest('PUT', `/payments/${id}`, data),
  deletePayment: (id) => apiRequest('DELETE', `/payments/${id}`),
};
