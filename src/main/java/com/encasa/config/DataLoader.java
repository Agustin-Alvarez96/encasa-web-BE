package com.encasa.config;

import com.encasa.models.Professional;
import com.encasa.models.Service;
import com.encasa.models.User;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.repositories.ServiceRepository;
import com.encasa.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ServiceRepository serviceRepository;
    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(ServiceRepository serviceRepository,
                      ProfessionalRepository professionalRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.serviceRepository = serviceRepository;
        this.professionalRepository = professionalRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("desipiodante@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("desipiodante@gmail.com");
            admin.setPassword(passwordEncoder.encode("Admin1234!"));
            admin.setRole("ADMIN");
            admin.setName("Admin");
            userRepository.save(admin);
        }
        if (serviceRepository.count() == 0) {
            serviceRepository.saveAll(List.of(
                new Service("electricidad", "Electricidad", "Instalaciones, tableros y emergencias. Matriculados.", "⚡", "bg-yellow-500"),
                new Service("plomeria", "Plomería y Gas", "Pérdidas, destapes, termotanques. Urgencias 24hs.", "🔧", "bg-blue-500"),
                new Service("aire-acondicionado", "Aire Acondicionado", "Instalación, service y reparación. Todas las marcas.", "❄️", "bg-cyan-500"),
                new Service("pintura", "Pintura", "Pintura, empapelado y revestimientos. Con garantía.", "🎨", "bg-purple-500"),
                new Service("carpinteria", "Carpintería", "Muebles a medida, placares y reparaciones.", "🔨", "bg-amber-500"),
                new Service("limpieza", "Limpieza", "Limpieza profunda, mudanzas y post-obra.", "🧹", "bg-green-500"),
                new Service("cerrajeria", "Cerrajería", "Apertura 24hs, cambio de cerraduras y copias.", "🔑", "bg-gray-500"),
                new Service("jardineria", "Jardinería", "Diseño, mantenimiento y parquización.", "🌱", "bg-emerald-500")
            ));
        }

        if (professionalRepository.count() == 0) {
            professionalRepository.saveAll(List.of(
                new Professional("Roberto Martínez", "Electricidad", "electricidad", 4.9, 47, 6500, "👨‍🔧", "Centro, Mar del Plata",
                        "Electricista matriculado con 18 años de experiencia. Especializado en tableros, instalaciones completas y reparaciones urgentes.", 18, true, "disponible"),
                new Professional("Claudia Fernández", "Limpieza", "limpieza", 5.0, 89, 3500, "👩‍💼", "Güemes, Mar del Plata",
                        "Servicio de limpieza profesional con más de 10 años en Mar del Plata. Trabajo con mis propios productos ecológicos.", 10, true, "disponible"),
                new Professional("Jorge Suárez", "Plomería", "plomeria", 4.8, 62, 7000, "👨‍🔧", "Los Troncos, Mar del Plata",
                        "Plomero gasista matriculado. Atiendo emergencias 24hs. Especialista en termotanques, calderas y gas natural.", 15, true, "ocupado"),
                new Professional("Ana García", "Pintura", "pintura", 4.9, 51, 5500, "👩‍🎨", "La Perla, Mar del Plata",
                        "Pintora profesional. Trabajos en departamentos, casas y locales comerciales. Pintura, empapelado y revestimientos.", 12, true, "disponible"),
                new Professional("Martín Rodríguez", "Carpintería", "carpinteria", 4.7, 38, 6000, "👨‍🔧", "Constitución, Mar del Plata",
                        "Carpintero especializado en muebles a medida, placares y revestimientos. 20 años de experiencia.", 20, true, "disponible"),
                new Professional("Laura Sánchez", "Aire Acondicionado", "aire-acondicionado", 4.8, 44, 8000, "👩‍🔧", "Punta Mogotes, Mar del Plata",
                        "Técnica matriculada en climatización. Instalación, service y reparación de aires acondicionados.", 8, true, "disponible"),
                new Professional("Diego Torres", "Cerrajería", "cerrajeria", 4.9, 73, 4500, "👨‍🔧", "Puerto, Mar del Plata",
                        "Cerrajero 24hs. Apertura de puertas, cambio de cerraduras y copias. Llego en 30 minutos a toda Mar del Plata.", 14, true, "disponible"),
                new Professional("Patricia González", "Jardinería", "jardineria", 4.8, 29, 4000, "👩‍🌾", "Camet, Mar del Plata",
                        "Paisajista y jardinera. Diseño, mantenimiento y parquización. Especializada en jardines de casa en la costa.", 9, true, "disponible")
            ));
        }
    }
}
