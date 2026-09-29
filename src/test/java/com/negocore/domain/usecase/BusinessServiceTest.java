package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.spi.IBusinessPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long BUSINESS_ID = 10L;

    @Mock private IBusinessPersistencePort businessPersistencePort;
    @Mock private IAuthenticationServicePort authenticationServicePort;

    @InjectMocks private BusinessService businessService;

    private Business ownedBusiness() {
        Business business = new Business();
        business.setId(BUSINESS_ID);
        business.setOwnerId(USER_ID);
        return business;
    }

    @Test
    @DisplayName("createBusiness asigna el ownerId del usuario autenticado y lo marca activo")
    void createBusiness_setsOwnerIdFromCurrentUser() {
        when(authenticationServicePort.getCurrentUserId()).thenReturn(USER_ID);
        when(businessPersistencePort.saveBusiness(any(Business.class))).thenAnswer(inv -> inv.getArgument(0));

        Business business = new Business();
        business.setName("Mi negocio");
        business.setCurrency("COP");

        Business saved = businessService.createBusiness(business);

        assertThat(saved.getOwnerId()).isEqualTo(USER_ID);
        assertThat(saved.getActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("createBusiness sin currency cae a la moneda por defecto")
    void createBusiness_blankCurrency_defaultsCurrency() {
        when(authenticationServicePort.getCurrentUserId()).thenReturn(USER_ID);
        when(businessPersistencePort.saveBusiness(any(Business.class))).thenAnswer(inv -> inv.getArgument(0));

        Business business = new Business();
        business.setName("Mi negocio");

        Business saved = businessService.createBusiness(business);

        assertThat(saved.getCurrency()).isEqualTo(DomainConstants.DEFAULT_CURRENCY);
    }

    @Test
    @DisplayName("findAllBusinesses delega en el puerto con el id del usuario actual")
    void findAllBusinesses_delegatesWithCurrentUserId() {
        when(authenticationServicePort.getCurrentUserId()).thenReturn(USER_ID);
        when(businessPersistencePort.findAllByOwnerId(USER_ID)).thenReturn(List.of(ownedBusiness()));

        List<Business> result = businessService.findAllBusinesses();

        assertThat(result).hasSize(1);
        verify(businessPersistencePort).findAllByOwnerId(eq(USER_ID));
    }

    @Test
    @DisplayName("updateBusiness sobre un negocio inexistente lanza NotFoundException")
    void updateBusiness_notFound_throwsNotFound() {
        when(authenticationServicePort.getCurrentUserId()).thenReturn(USER_ID);
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> businessService.updateBusiness(BUSINESS_ID, new Business()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.BUSINESS_NOT_FOUND);
    }

    @Test
    @DisplayName("updateBusiness sobre un negocio de otro dueño lanza el mismo NotFoundException")
    void updateBusiness_nonOwner_throwsNotFound() {
        when(authenticationServicePort.getCurrentUserId()).thenReturn(USER_ID);
        Business other = ownedBusiness();
        other.setOwnerId(999L);
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> businessService.updateBusiness(BUSINESS_ID, new Business()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.BUSINESS_NOT_FOUND);
    }

    @Test
    @DisplayName("updateBusiness solo aplica los campos no nulos del request")
    void updateBusiness_appliesOnlyNonNullFields() {
        when(authenticationServicePort.getCurrentUserId()).thenReturn(USER_ID);
        Business existing = ownedBusiness();
        existing.setName("Nombre viejo");
        existing.setAddress("Dirección vieja");
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(existing));
        when(businessPersistencePort.saveBusiness(any(Business.class))).thenAnswer(inv -> inv.getArgument(0));

        Business changes = new Business();
        changes.setName("Nombre nuevo");

        ArgumentCaptor<Business> captor = ArgumentCaptor.forClass(Business.class);
        businessService.updateBusiness(BUSINESS_ID, changes);

        verify(businessPersistencePort).saveBusiness(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Nombre nuevo");
        assertThat(captor.getValue().getAddress()).isEqualTo("Dirección vieja");
    }
}
