package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryCandidateRepository implements CandidateRepository {

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0,
                "Alexander",
                "Python Developer",
                LocalDateTime.of(2024, 8, 13, 10, 30)));
        save(new Candidate(0,
                "Stepan",
                "Junior Java Developer",
                LocalDateTime.of(2024, 7, 12, 11, 45)));
        save(new Candidate(0,
                "Ivan",
                "Ruby Developer",
                LocalDateTime.of(2024, 7, 12, 11, 45)));
        save(new Candidate(0,
                "Nikita",
                "Senior Java Developer",
                LocalDateTime.of(2024, 5, 6, 12, 40)));
        save(new Candidate(0,
                "Natasha",
                "Frontend Developer",
                LocalDateTime.of(2024, 4, 16, 7, 18)));
        save(new Candidate(0,
                "Lida",
                "Scala Developer",
                LocalDateTime.of(2024, 8, 15, 13, 26)));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldVacancy) -> new Candidate(oldVacancy.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
