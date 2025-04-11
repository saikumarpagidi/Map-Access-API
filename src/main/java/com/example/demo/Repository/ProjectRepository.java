package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

	List<Project> findByUserInstituteId(Long instituteId);

	long countByUserInstituteId(Long instituteId);
}
