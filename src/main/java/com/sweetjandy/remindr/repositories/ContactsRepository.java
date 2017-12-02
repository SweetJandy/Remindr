package com.sweetjandy.remindr.repositories;

import com.sweetjandy.remindr.models.Contact;

import com.sweetjandy.remindr.models.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.transaction.Transactional;
import java.util.List;

public interface ContactsRepository extends CrudRepository<Contact, Long> {

    List<Contact> findByIdIn (List<Long> contactsIds);

    @Query(value = "SELECT * from remindr_contact WHERE remindr_id = ?1", nativeQuery = true)
    List<Contact> findForRemindr(long id);

    Contact findByPhoneNumber (String phoneNumber);

}

