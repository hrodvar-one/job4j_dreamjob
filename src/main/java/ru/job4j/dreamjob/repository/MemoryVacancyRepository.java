package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0,
                        "Intern Java Developer",
                        "Набираем Intern Java Developer",
                        LocalDateTime.of(2024, 8, 13, 10, 30),
                        true,
                        1));
        save(new Vacancy(0,
                        "Junior Java Developer",
                        "Ищем Junior Java Developer",
                        LocalDateTime.of(2024, 7, 12, 11, 45),
                        true,
                        2));
        save(new Vacancy(0,
                        "Junior+ Java Developer",
                        "Мы молодая быстроразвивающаяся компания в поиске Junior+ Java Developer",
                        LocalDateTime.of(2024, 7, 12, 11, 45),
                        true,
                        3));
        save(new Vacancy(0,
                        "Senior Java Developer",
                        "Ищем Senior Java Developer зп 400к",
                        LocalDateTime.of(2024, 5, 6, 12, 40),
                        true,
                        1));
        save(new Vacancy(0,
                        "Middle+ Java Developer",
                        "Стартап в поиске Middle+ Java Developer",
                        LocalDateTime.of(2024, 4, 16, 7, 18),
                        true,
                        2));
        save(new Vacancy(0,
                        "Senior Java Developer",
                        "В самый крупный банк России требуется Senior Java Developer",
                        LocalDateTime.of(2024, 8, 15, 13, 26),
                        true,
                        3));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        if (vacancies.containsKey(id)) {
            vacancies.remove(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(),
                                                vacancy.getTitle(),
                                                vacancy.getDescription(),
                                                vacancy.getCreationDate(),
                                                vacancy.getVisible(),
                                                vacancy.getCityId())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
