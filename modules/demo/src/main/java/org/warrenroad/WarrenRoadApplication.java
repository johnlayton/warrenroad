package org.warrenroad;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableJpaRepositories(
        considerNestedRepositories = true
)
public class WarrenRoadApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarrenRoadApplication.class, args);
    }


    @Component
    public class Setup implements CommandLineRunner {

        private static final Logger LOGGER = LoggerFactory.getLogger(Setup.class);

        private final A.ARepository aRepository;
        private final B.BRepository bRepository;
        private final C.CRepository cRepository;

        public Setup(final A.ARepository aRepository,
                     final B.BRepository bRepository,
                     final C.CRepository cRepository) {
            this.aRepository = aRepository;
            this.bRepository = bRepository;
            this.cRepository = cRepository;
        }

        @Override
        @Transactional
        public void run(String... args) throws Exception {
            LOGGER.info("Run Setup ...");

            final List<A> as = aRepository.saveAll(List.of(new A("1"), new A("2"), new A("3")));
            final List<B> bs = bRepository.saveAll(List.of(new B("1"), new B("2"), new B("3")));
            final List<C> cs = cRepository.saveAll(List.of(new C("1"), new C("2"), new C("3")));

            as.forEach(a -> bs.forEach(a::add));
            cs.forEach(c -> bs.forEach(c::add));
            aRepository.saveAll(as);
            cRepository.saveAll(cs);
        }
    }

    @Entity(name = "A")
    public static class A {

        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        private Long id;
        @ManyToMany
        @JoinTable(name = "A_B")
        private final Set<B> bs = new HashSet<>();
        private String code;

        public A() {
        }

        public A(String code) {
            this.code = code;
        }

        public void add(final B b) {
            bs.add(b);
        }

        public void remove(final B b) {
            bs.remove(b);
        }

        @Override
        public String toString() {
            return "A{id=" + id + ", code='" + code + "'}";
        }

        @RepositoryRestResource(collectionResourceRel = "a", path = "a")
        public interface ARepository extends JpaRepository<A, Long> {
        }
    }


    @Entity(name = "B")
    public static class B {

        private static final Logger LOGGER = LoggerFactory.getLogger(B.class);
        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        private Long id;
        @ManyToMany(mappedBy = "bs")
        private final Set<A> as = new HashSet<>();
        @ManyToMany(mappedBy = "bs")
        private final Set<C> cs = new HashSet<>();
        private String code;

        public B() {
        }

        public B(String code) {
            this.code = code;
        }

        @PreRemove
        private void removeFromOwners() {
            for (A a : as) {
                LOGGER.info("Remove {} from {}", this, a);
                a.remove(this);
            }
            for (C c : cs) {
                LOGGER.info("Remove {} from {}", this, c);
                c.remove(this);
            }
        }

        @Override
        public String toString() {
            return "B{id=" + id + ", code='" + code + "'}";
        }

        @RepositoryRestResource(collectionResourceRel = "b", path = "b")
        public interface BRepository extends JpaRepository<B, Long> {
        }
    }

    @Entity(name = "C")
    public static class C {

        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        private Long id;
        @ManyToMany
        @JoinTable(name = "C_B")
        private final Set<B> bs = new HashSet<>();
        private String code;

        public C() {
        }

        public C(String code) {
            this.code = code;
        }

        public void add(final B b) {
            bs.add(b);
        }

        public void remove(final B b) {
            bs.remove(b);
        }

        @Override
        public String toString() {
            return "C{id=" + id + ", code='" + code + "'}";
        }

        @RepositoryRestResource(collectionResourceRel = "c", path = "c")
        public interface CRepository extends JpaRepository<C, Long> {
        }
    }
}
