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

     Contact findByPhoneNumber (String phoneNumber);

     //@Query(value = "SELECT * FROM contacts JOIN users ON contacts.id = users.contact_id WHERE contact_id = ?1",
    //nativeQuery = true)
//     @Query("SELECT c FROM Contact c JOIN c.users u WHERE u.id = ?1 AND c.id = ?2")
//     Contact findContactFor(long userId, long contactId);

//     @Query("SELECT c FROM Contact c JOIN c.users u Where u.id = ?1")
//     List<Contact> findAllContactsFor(long userId);

     //Contact findByUsersAndId(User user, long id);

     @Query(value = "SELECT * FROM contacts JOIN user_contact ON user_contact.user_id=?1 AND contacts.id = user_contact.contacts_id", nativeQuery = true)
     List<Contact> getContactList(long userId);

     @Transactional
     @Modifying
     @Query(value = "INSERT INTO user_contact (user_id, contacts_id) VALUES (?1, ?2)", nativeQuery = true)
     void addContactToList(long userId, long contactId);

     @Transactional
     @Modifying
     @Query(value = "DELETE FROM user_contact WHERE user_id = ?1 AND contacts_id = ?2", nativeQuery = true)
     void deleteContactFromList();

}

