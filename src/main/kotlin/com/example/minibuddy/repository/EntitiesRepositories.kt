package com.example.minibuddy.repository

import com.example.minibuddy.model.Phrase
import com.example.minibuddy.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface UserRepository : ReactiveCrudRepository<User, Long> {}

interface PhraseRepository : ReactiveCrudRepository<Phrase, Long> {}
