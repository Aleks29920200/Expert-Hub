package com.example.skillsh.repository;

import com.example.skillsh.domain.entity.Appointment;
import com.example.skillsh.domain.entity.Block;
import com.example.skillsh.domain.entity.ServiceOffer;
import com.example.skillsh.domain.entity.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
    @Override
    List<Appointment> findAll();
}


