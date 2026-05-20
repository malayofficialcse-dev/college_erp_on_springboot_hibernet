import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Row, Col, Badge } from 'react-bootstrap';
import api from '../../services/api';

const Fees = () => {
  const [payments, setPayments] = useState([]);
  const [students, setStudents] = useState([]);
  const [totalCollected, setTotalCollected] = useState(0);
  
  const [showPayModal, setShowPayModal] = useState(false);
  const [currentPayment, setCurrentPayment] = useState({
    studentId: '', amountPaid: '', feeType: 'TUITION', semester: '', remarks: ''
  });

  useEffect(() => {
    fetchPayments();
    fetchStudents();
    fetchTotalCollected();
  }, []);

  const fetchPayments = async () => {
    try {
      const response = await api.get('/fees');
      setPayments(response.data.content || response.data);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchStudents = async () => {
    try {
      const response = await api.get('/students/search', { params: { size: 100 } });
      setStudents(response.data.content || []);
    } catch (e) {
      console.error(e);
    }
  };

  const fetchTotalCollected = async () => {
    try {
      const response = await api.get('/fees/total-collected');
      setTotalCollected(response.data.totalCollected || 0);
    } catch (e) {
      console.error(e);
    }
  };

  const handlePay = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        student: { id: parseInt(currentPayment.studentId) },
        amountPaid: parseFloat(currentPayment.amountPaid),
        feeType: currentPayment.feeType,
        semester: currentPayment.semester,
        paymentDate: new Date().toISOString().split('T')[0],
        status: 'PAID',
        remarks: currentPayment.remarks
      };
      await api.post('/fees', payload);
      setShowPayModal(false);
      fetchPayments();
      fetchTotalCollected();
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <div className="container-fluid">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="text-dark fw-bold mb-0">Fees Management</h2>
          <small className="text-muted">Total Collections: <strong>${totalCollected}</strong></small>
        </div>
        <Button variant="primary" onClick={() => { setCurrentPayment({ studentId: '', amountPaid: '', feeType: 'TUITION', semester: '', remarks: '' }); setShowPayModal(true); }}>
          <i className="bi bi-cash-stack me-2"></i>Record Payment
        </Button>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body p-0">
          <Table responsive hover className="mb-0 align-middle">
            <thead className="bg-light">
              <tr>
                <th className="px-4 py-3">Student Name</th>
                <th>Fee Type</th>
                <th>Semester</th>
                <th>Amount Paid</th>
                <th>Payment Date</th>
                <th>Status</th>
                <th>Remarks</th>
              </tr>
            </thead>
            <tbody>
              {payments.map(p => (
                <tr key={p.id}>
                  <td className="px-4 fw-bold">{p.student?.firstName} {p.student?.lastName}</td>
                  <td>{p.feeType}</td>
                  <td>{p.semester}</td>
                  <td>${p.amountPaid}</td>
                  <td>{p.paymentDate}</td>
                  <td>
                    <Badge bg="success">PAID</Badge>
                  </td>
                  <td>{p.remarks}</td>
                </tr>
              ))}
              {payments.length === 0 && (
                <tr><td colSpan="7" className="text-center py-4 text-muted">No fee payments recorded.</td></tr>
              )}
            </tbody>
          </Table>
        </div>
      </div>

      {/* Record Fee Payment Modal */}
      <Modal show={showPayModal} onHide={() => setShowPayModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Record Fee Payment</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handlePay}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Student</Form.Label>
              <Form.Select name="studentId" value={currentPayment.studentId} onChange={e => setCurrentPayment({...currentPayment, studentId: e.target.value})} required>
                <option value="">Select Student</option>
                {students.map(st => (
                  <option key={st.id} value={st.id}>{st.firstName} {st.lastName}</option>
                ))}
              </Form.Select>
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Fee Type</Form.Label>
                  <Form.Select name="feeType" value={currentPayment.feeType} onChange={e => setCurrentPayment({...currentPayment, feeType: e.target.value})}>
                    <option value="TUITION">Tuition Fee</option>
                    <option value="EXAM">Exam Fee</option>
                    <option value="HOSTEL">Hostel Fee</option>
                    <option value="TRANSPORT">Transport Fee</option>
                    <option value="LIBRARY">Library Fee</option>
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Amount Paid</Form.Label>
                  <Form.Control type="number" step="0.01" name="amountPaid" value={currentPayment.amountPaid} onChange={e => setCurrentPayment({...currentPayment, amountPaid: e.target.value})} required />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Semester</Form.Label>
              <Form.Control type="text" name="semester" value={currentPayment.semester} onChange={e => setCurrentPayment({...currentPayment, semester: e.target.value})} placeholder="e.g. Semester I 2025-26" required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Remarks</Form.Label>
              <Form.Control type="text" name="remarks" value={currentPayment.remarks} onChange={e => setCurrentPayment({...currentPayment, remarks: e.target.value})} />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowPayModal(false)}>Cancel</Button>
            <Button variant="primary" type="submit">Record Payment</Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};

export default Fees;
