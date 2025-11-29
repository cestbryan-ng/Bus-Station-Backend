package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.Coupon;

import java.util.List;

@Repository
public interface CouponRepository extends CassandraRepository<Coupon, UUID> {

  @AllowFiltering
  List<Coupon> findAllByIdHistorique(UUID idHistorique);
}
