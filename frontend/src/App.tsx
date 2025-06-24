import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/common/Layout';
import Dashboard from './components/dashboard/Dashboard';
import RolesList from './components/roles/RolesList';
import CreateRole from './components/roles/CreateRole';
import EditRole from './components/roles/EditRole';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="roles" element={<RolesList />} />
          <Route path="roles/create" element={<CreateRole />} />
          <Route path="roles/:id/edit" element={<EditRole />} />
          <Route path="users" element={<div className="p-6"><h1>Users Management - Coming Soon</h1></div>} />
          <Route path="permissions" element={<div className="p-6"><h1>Permissions Management - Coming Soon</h1></div>} />
          <Route path="audit" element={<div className="p-6"><h1>Audit Logs - Coming Soon</h1></div>} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;