package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Payroll;
import com.example.demo.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PayrollService {

    @Autowired private PayrollRepository payrollRepository;

    public Page<Payroll> getAllPayrolls(Pageable pageable) {
        return payrollRepository.findAll(pageable);
    }

    public Payroll getById(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll", "id", id));
    }

    public Page<Payroll> getByEmployee(Long employeeId, Pageable pageable) {
        return payrollRepository.findByEmployeeId(employeeId, pageable);
    }

    public Page<Payroll> getByMonthYear(Integer month, Integer year, Pageable pageable) {
        return payrollRepository.findByPayMonthAndPayYear(month, year, pageable);
    }

    public BigDecimal getTotalPayrollByMonthYear(Integer month, Integer year) {
        BigDecimal total = payrollRepository.sumNetSalaryByMonthAndYear(month, year);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public Payroll generatePayroll(Payroll payroll) {
        // Check duplicate for same employee+month+year
        if (payrollRepository.findByEmployeeIdAndPayMonthAndPayYear(
                payroll.getEmployee().getId(), payroll.getPayMonth(), payroll.getPayYear()).isPresent()) {
            throw new IllegalArgumentException("Payroll already generated for this employee and month.");
        }
        // Compute gross and net salary
        BigDecimal gross = payroll.getBasicSalary()
                .add(safe(payroll.getHra()))
                .add(safe(payroll.getDa()))
                .add(safe(payroll.getTa()))
                .add(safe(payroll.getOtherAllowances()));
        payroll.setGrossSalary(gross);

        BigDecimal net = gross
                .subtract(safe(payroll.getPfDeduction()))
                .subtract(safe(payroll.getTaxDeduction()))
                .subtract(safe(payroll.getOtherDeductions()));
        payroll.setNetSalary(net);

        return payrollRepository.save(payroll);
    }

    @Transactional
    public Payroll updateStatus(Long id, String status) {
        Payroll payroll = getById(id);
        payroll.setStatus(status);
        return payrollRepository.save(payroll);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        payrollRepository.deleteById(id);
    }

    private BigDecimal safe(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }
}
