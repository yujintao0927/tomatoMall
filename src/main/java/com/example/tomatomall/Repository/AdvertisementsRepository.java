package com.example.tomatomall.Repository;

import com.example.tomatomall.po.Advertisements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.criteria.CriteriaBuilder;

public interface AdvertisementsRepository extends JpaRepository<Advertisements, Integer> {
}
