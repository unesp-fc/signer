package br.unesp.fc.signer.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final Service service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var pessoa = service.getPessoa(username);
        if (pessoa == null) {
            throw new UsernameNotFoundException("User not found!");
        }
        return new Usuario(pessoa);
    }

}
