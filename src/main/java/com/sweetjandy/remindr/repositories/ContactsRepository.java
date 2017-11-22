package com.sweetjandy.remindr.repositories;

import com.sweetjandy.remindr.models.Contact;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContactsRepository extends CrudRepository<Contact, Long> {
    public Contact findByPhoneNumber (String phoneNumber);

    @Query("SELECT c FROM Contact c JOIN c.users u WHERE u.id = ?1 AND c.id = ?2")
    Contact findContactFor(long userId, long contactId);

    @Query("SELECT c FROM Contact c JOIN c.users u Where u.id = ?1")
    List<Contact> findAllContactsFor(long userId);
}
