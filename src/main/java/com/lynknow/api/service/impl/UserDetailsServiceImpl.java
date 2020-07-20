package com.lynknow.api.service.impl;

import com.lynknow.api.model.UserData;
import com.lynknow.api.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDataService userDataService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserData user = userDataService.getByUsername(s);

        if (user != null) {
            return user;
        }

        throw new UsernameNotFoundException("User not found");
    }

}
