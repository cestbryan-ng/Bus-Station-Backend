package com.enspy26.gi.database_agence_voyage.repositories;

import java.util.UUID;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.enspy26.gi.database_agence_voyage.models.User;

import java.util.List;

@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {

  @AllowFiltering
  List<User> findByEmail(String email);

  @AllowFiltering
  List<User> findByUsername(String username);

  @AllowFiltering
  boolean existsByEmail(String email);

  @AllowFiltering
  boolean existsByTelNumber(String telNumber);
}
