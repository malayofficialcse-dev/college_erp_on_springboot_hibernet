import React, { useContext } from 'react';
import { Row, Col, Card } from 'react-bootstrap';
import { AuthContext } from '../../context/AuthContext';

const Dashboard = () => {
  const { user } = useContext(AuthContext);

  return (
    <div>
      <h2 className="mb-4 text-dark fw-bold">Dashboard</h2>
      <div className="alert alert-primary mb-4" role="alert">
        Welcome back, <strong>{user?.username}</strong>! Here is an overview of the College ERP system.
      </div>
      
      <Row className="g-4 mb-4">
        <Col md={6} xl={3}>
          <Card className="bg-primary text-white h-100 shadow-sm border-0">
            <Card.Body>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <h6 className="text-uppercase fw-bold text-white-50 mb-1">Total Students</h6>
                  <h2 className="mb-0">1,245</h2>
                </div>
                <div className="bg-white bg-opacity-25 p-3 rounded">
                  <i className="bi bi-people fs-4"></i>
                </div>
              </div>
            </Card.Body>
            <Card.Footer className="bg-transparent border-0 d-flex align-items-center justify-content-between pb-3">
              <small className="text-white-50">View Details</small>
              <small className="text-white"><i className="bi bi-arrow-right"></i></small>
            </Card.Footer>
          </Card>
        </Col>
        <Col md={6} xl={3}>
          <Card className="bg-success text-white h-100 shadow-sm border-0">
            <Card.Body>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <h6 className="text-uppercase fw-bold text-white-50 mb-1">Active Courses</h6>
                  <h2 className="mb-0">42</h2>
                </div>
                <div className="bg-white bg-opacity-25 p-3 rounded">
                  <i className="bi bi-book fs-4"></i>
                </div>
              </div>
            </Card.Body>
            <Card.Footer className="bg-transparent border-0 d-flex align-items-center justify-content-between pb-3">
              <small className="text-white-50">View Details</small>
              <small className="text-white"><i className="bi bi-arrow-right"></i></small>
            </Card.Footer>
          </Card>
        </Col>
        <Col md={6} xl={3}>
          <Card className="bg-warning text-white h-100 shadow-sm border-0">
            <Card.Body>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <h6 className="text-uppercase fw-bold text-white-50 mb-1">Employees</h6>
                  <h2 className="mb-0">128</h2>
                </div>
                <div className="bg-white bg-opacity-25 p-3 rounded">
                  <i className="bi bi-person-badge fs-4"></i>
                </div>
              </div>
            </Card.Body>
            <Card.Footer className="bg-transparent border-0 d-flex align-items-center justify-content-between pb-3">
              <small className="text-white-50">View Details</small>
              <small className="text-white"><i className="bi bi-arrow-right"></i></small>
            </Card.Footer>
          </Card>
        </Col>
        <Col md={6} xl={3}>
          <Card className="bg-danger text-white h-100 shadow-sm border-0">
            <Card.Body>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <h6 className="text-uppercase fw-bold text-white-50 mb-1">Pending Fees</h6>
                  <h2 className="mb-0">$45K</h2>
                </div>
                <div className="bg-white bg-opacity-25 p-3 rounded">
                  <i className="bi bi-cash-stack fs-4"></i>
                </div>
              </div>
            </Card.Body>
            <Card.Footer className="bg-transparent border-0 d-flex align-items-center justify-content-between pb-3">
              <small className="text-white-50">View Details</small>
              <small className="text-white"><i className="bi bi-arrow-right"></i></small>
            </Card.Footer>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
