package com.example.student_management.repository;

import com.example.student_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    // Recherche par ID
    Student findById(int id);

    // Statistiques par année de naissance (PostgreSQL compatible)
    @Query("SELECT EXTRACT(YEAR FROM s.dateNaissance), COUNT(s) " +
            "FROM Student s GROUP BY EXTRACT(YEAR FROM s.dateNaissance)")
    Collection<Object[]> findNbrStudentByYear();
}