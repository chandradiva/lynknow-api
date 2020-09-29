package com.lynknow.api.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_data", schema = "public")
public class UserData implements UserDetails, Serializable {

    @Id
    @Column(name = "id", unique = true)
    @SequenceGenerator(name = "pk_sequence", sequenceName = "user_data_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "verification_point")
    private int verificationPoint = 0;

    @ManyToOne
    @JoinColumn(name = "current_subscription_package_id")
    private SubscriptionPackage currentSubscriptionPackage;

    @ManyToOne
    @JoinColumn(name = "role_data_id")
    private RoleData roleData;

    @Column(name = "join_date")
    private Date joinDate;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "deleted_date")
    private Date deletedDate;

    @Column(name = "is_active")
    private int isActive = 1;

    @Column(name = "max_verification_credit")
    private int maxVerificationCredit = 0;

    @Column(name = "current_verification_credit")
    private int currentVerificationCredit = 0;

    @Column(name = "fb_id")
    private String fbId;

    @Column(name = "fb_email")
    private String fbEmail;

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "google_email")
    private String googleEmail;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "expired_token")
    private Date expiredToken;

    @Column(name = "max_total_view")
    private int maxTotalView = 500;

    @Column(name = "used_total_view")
    private int usedTotalView = 0;

    @Column(name = "temp_email")
    private String tempEmail;

    @Column(name = "expired_premium_date")
    private Date expiredPremiumDate;

    @Column(name = "expired_total_view")
    private Date expiredTotalView;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

}
