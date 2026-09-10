package com.encasa.catalog;

import com.encasa.dto.ServiceDto;

import java.util.List;

public class ServiceCatalog {

    public static final List<ServiceDto> ALL = List.of(
            new ServiceDto("electricidad", "Electricidad", "Instalaciones, tableros y emergencias. Matriculados."),
            new ServiceDto("plomeria", "Plomería y Gas", "Pérdidas, destapes, termotanques. Urgencias 24hs."),
            new ServiceDto("aire-acondicionado", "Aire Acondicionado", "Instalación, service y reparación. Todas las marcas."),
            new ServiceDto("pintura", "Pintura", "Pintura, empapelado y revestimientos. Con garantía."),
            new ServiceDto("carpinteria", "Carpintería", "Muebles a medida, placares y reparaciones."),
            new ServiceDto("limpieza", "Limpieza", "Limpieza profunda, mudanzas y post-obra."),
            new ServiceDto("cerrajeria", "Cerrajería", "Apertura 24hs, cambio de cerraduras y copias."),
            new ServiceDto("jardineria", "Jardinería", "Diseño, mantenimiento y parquización.")
    );

    public static String nameOf(String serviceId) {
        return ALL.stream()
                .filter(s -> s.id().equals(serviceId))
                .map(ServiceDto::name)
                .findFirst()
                .orElse(serviceId);
    }

    private ServiceCatalog() {
    }
}
