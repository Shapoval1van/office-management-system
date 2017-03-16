package com.netcracker.service.security;


import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final PersonRepository personRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public UserDetailServiceImpl(PersonRepository personRepository,
                                 RoleRepository roleRepository) {
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Person user = personRepository.findPersonByEmail(s)
                .orElseThrow(() -> new UsernameNotFoundException(s));

        Role role = roleRepository.findOne(user.getRole().getId())
                .orElseThrow(() -> new UsernameNotFoundException(s));

        user.setRole(role);

        return user;
    }
}