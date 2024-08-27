package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0,
                "Alexander",
                "Python Developer",
                LocalDateTime.of(2024, 8, 13, 10, 30),
                1,
                0));
        save(new Candidate(0,
                "Stepan",
                "Junior Java Developer",
                LocalDateTime.of(2024, 7, 12, 11, 45),
                2,
                0));
        save(new Candidate(0,
                "Ivan",
                "Ruby Developer",
                LocalDateTime.of(2024, 7, 12, 11, 45),
                3,
                0));
        save(new Candidate(0,
                "Nikita",
                "Senior Java Developer",
                LocalDateTime.of(2024, 5, 6, 12, 40),
                1,
                0));
        save(new Candidate(0,
                "Natasha",
                "Frontend Developer",
                LocalDateTime.of(2024, 4, 16, 7, 18),
                2,
                0));
        save(new Candidate(0,
                "Lida",
                "Scala Developer",
                LocalDateTime.of(2024, 8, 15, 13, 26),
                3,
                0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        if (candidates.containsKey(id)) {
            candidates.remove(id);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(oldCandidate.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate(),
                        candidate.getCityId(),
                        candidate.getFileId())) != null;
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
