package com.sweetjandy.remindr.repositories;

import com.sweetjandy.remindr.models.Contact;

import org.springframework.data.repository.CrudRepository;

public interface ContactsRepository extends CrudRepository<Contact, Long> {
     Contact findByPhoneNumber (String phoneNumber);
}
