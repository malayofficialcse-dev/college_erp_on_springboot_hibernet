import React, { useContext, useState } from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { Navbar, Container, Nav, Dropdown } from 'react-bootstrap';
import { AuthContext } from '../context/AuthContext';
import './Layout.css';
import 'bootstrap-icons/font/bootstrap-icons.css';

const Layout = () => {
  const { user, logout } = useContext(AuthContext);
  const location = useLocation();
  const [isSidebarToggled, setSidebarToggled] = useState(false);

  const toggleSidebar = () => setSidebarToggled(!isSidebarToggled);

  const navItems = [
    { path: '/dashboard', name: 'Dashboard', icon: 'bi-grid-1x2-fill' },
    { path: '/students', name: 'Students', icon: 'bi-mortarboard-fill' },
    { path: '/employees', name: 'Employees', icon: 'bi-people-fill' },
    { path: '/courses', name: 'Courses', icon: 'bi-journal-bookmark-fill' },
    { path: '/attendance', name: 'Attendance', icon: 'bi-calendar-check-fill' },
    { path: '/departments', name: 'Departments', icon: 'bi-building' },
  ];

  return (
    <div className={`d-flex ${isSidebarToggled ? 'toggled' : ''}`} id="wrapper">
      {/* Sidebar */}
      <div className="bg-dark text-white shadow-lg" id="sidebar-wrapper">
        <div className="sidebar-heading">
          <i className="bi bi-hexagon-fill me-2 text-primary"></i>
          ERP Pro
        </div>
        <div className="list-group list-group-flush mt-3">
          {navItems.map((item) => (
            <Link 
              key={item.path}
              to={item.path} 
              className={`list-group-item list-group-item-action bg-transparent text-white ${location.pathname.startsWith(item.path) ? 'active-link' : ''}`}
            >
              <i className={`bi ${item.icon} fs-5`}></i>
              {item.name}
            </Link>
          ))}
        </div>
      </div>

      {/* Page Content */}
      <div id="page-content-wrapper">
        <Navbar expand="lg" className="top-navbar shadow-sm">
          <Container fluid>
            <button className="btn btn-light border-0 me-3" onClick={toggleSidebar}>
              <i className="bi bi-list fs-4"></i>
            </button>
            <Navbar.Collapse id="basic-navbar-nav" className="justify-content-end">
              <Nav>
                <Dropdown align="end">
                  <Dropdown.Toggle variant="transparent" id="dropdown-basic" className="d-flex align-items-center border-0 shadow-none">
                    <div className="me-3 text-end d-none d-md-block">
                      <div className="fw-bold mb-0 lh-1 text-dark">{user?.username || 'Admin User'}</div>
                      <small className="text-muted">{user?.roles?.[0] || 'Administrator'}</small>
                    </div>
                    <div className="user-avatar">
                      {user?.username?.charAt(0).toUpperCase() || 'A'}
                    </div>
                  </Dropdown.Toggle>
                  <Dropdown.Menu className="shadow border-0 rounded-3 mt-2">
                    <Dropdown.Item href="#/profile"><i className="bi bi-person me-2"></i>Profile</Dropdown.Item>
                    <Dropdown.Item href="#/settings"><i className="bi bi-gear me-2"></i>Settings</Dropdown.Item>
                    <Dropdown.Divider />
                    <Dropdown.Item onClick={logout} className="text-danger">
                      <i className="bi bi-box-arrow-right me-2"></i>Logout
                    </Dropdown.Item>
                  </Dropdown.Menu>
                </Dropdown>
              </Nav>
            </Navbar.Collapse>
          </Container>
        </Navbar>

        <div className="main-content">
          <div className="container-fluid">
            <Outlet />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Layout;
