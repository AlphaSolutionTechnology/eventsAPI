package com.alphasolutions.eventapi.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.alphasolutions.eventapi.model.Palestra;
import com.alphasolutions.eventapi.model.User;

public interface UserRepository extends JpaRepository<User, String> {

    // Métodos existentes (mantidos)
    User findByUniqueCode(String uniqueCode);
    Optional<User> findById(String id);
    boolean existsByUniqueCode(String uniqueCode);
    boolean existsByEmail(String email);
    boolean existsByPalestra(Optional<Palestra> palestra);
    
    // Método otimizado para login (com role e avatar)
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.evento WHERE u.email = :email")
    Optional<User> findByEmailWithAvatar(@Param("email") String email);
    
    // Novo método para buscar usuário por token JWT
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.evento WHERE u.id = " +
           "(SELECT j.userId FROM JwtToken j WHERE j.token = :token)")
    Optional<User> findByToken(@Param("token") String token);
    
    @Modifying
    @Query("UPDATE User u SET u.palestra = NULL WHERE u.palestra.id = :palestraId")
    void unsubscribeUsersFromPalestra(@Param("palestraId") Long palestraId);

    List<User> findByPalestra(Palestra palestra);
    List<User> findAllByPalestra(Palestra palestra);

    // Método para atualizar avatar
    @Modifying
    @Query("UPDATE User u SET u.avatarStyle = :newStyle, u.avatarSeed = :newSeed WHERE u.id = :userId")
    void updateUserAvatar(
        @Param("userId") String userId,
        @Param("newStyle") String newStyle,
        @Param("newSeed") String newSeed
    );

    // Método para buscar dados do avatar
    @Query("SELECT u.avatarStyle, u.avatarSeed FROM User u WHERE u.id = :userId")
    Optional<Object[]> findAvatarDataById(@Param("userId") String userId);

    // Novo método para verificar e buscar usuário completo
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.evento LEFT JOIN FETCH u.palestra WHERE u.id = :userId")
    Optional<User> findCompleteUserById(@Param("userId") String userId);
}