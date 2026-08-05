package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffMemberRepository
        extends JpaRepository<StaffMember, Long> {

    List<StaffMember> findAllByActiveTrueOrderByDisplayNameAsc();
}