package com.tesis.queseria_la_charito.entities.user;

import com.tesis.queseria_la_charito.entities.batch.BatchModificationEntity;
import com.tesis.queseria_la_charito.entities.production.ProductionEntity;
import com.tesis.queseria_la_charito.entities.stockControl.StockControlEntity;
import com.tesis.queseria_la_charito.entities.dispatch.DispatchEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "role_id")
  private RoleEntity role;

  @Column(name = "show")
  private Boolean show;

  @Column(name = "isDispatching")
  private Boolean isDispatching;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private List<ProductionEntity> lstProductions;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private List<BatchModificationEntity> lstBatchModifications;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private List<StockControlEntity> lstStockControls;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
  private List<DispatchEntity> lstDispatches;


  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Arrays.stream(role.getRole().split(","))
        .map(role -> new SimpleGrantedAuthority("ROLE_" + getRole()))
        .collect(Collectors.toList());
  }


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
}

