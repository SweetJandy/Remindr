package com.sweetjandy.remindr.repositories;

import com.sweetjandy.remindr.models.Contact;

import com.sweetjandy.remindr.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContactsRepository extends CrudRepository<Contact, Long> {
     Contact findByPhoneNumber (String phoneNumber);

     //@Query(value = "SELECT * FROM contacts JOIN users ON contacts.id = users.contact_id WHERE contact_id = ?1",
    //nativeQuery = true)
     @Query("SELECT c FROM Contact c JOIN c.users u WHERE u.id = ?1 AND c.id = ?2")
     Contact findContactFor(long userId, long contactId);

     //Contact findByUsersAndId(User user, long id);
}

