package com.example.demo.repositories;

import com.example.demo.models.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SizeRepository extends JpaRepository<Size, Long> {

    Optional<Size> findByName(String name);

    List<Size> findByCategory(String category);

    List<Size> findAllByOrderBySortOrderAsc();

    List<Size> findByNameIn(List<String> names);
}
