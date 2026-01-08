package com.notes.api.notes.service;

import com.notes.api.notes.domain.Note;
import com.notes.api.notes.repo.NoteRepository;
import com.notes.api.notes.web.dto.NoteCreateRequest;
import com.notes.api.notes.web.dto.NoteResponse;
import com.notes.api.notes.web.dto.NoteUpdateRequest;
import com.notes.api.shared.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    @Transactional
    public NoteResponse create(NoteCreateRequest request) {
        Note note = Note.builder()
                .title(request.title())
                .content(request.content())
                .status(Note.Status.OPEN)
                .build();

        Note saved = noteRepository.save(note);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> list() {
        return noteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private NoteResponse toResponse(Note n) {
        return new NoteResponse(
                n.getId(),
                n.getTitle(),
                n.getContent(),
                n.getStatus().name(),
                n.getCreatedAt(),
                n.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public NoteResponse getById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note con id " + id + " no existe"));
        return toResponse(note);
    }

    @Transactional
    public NoteResponse update(Long id, NoteUpdateRequest request) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Note con id " + id + " no existe"));

        note.setTitle(request.title());
        note.setContent(request.content());

        if (request.status() != null && !request.status().isBlank()) {
            note.setStatus(parseStatus(request.status()));
        }

        return toResponse(noteRepository.save(note));
    }

    @Transactional
    public void delete(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new NotFoundException("Note con id " + id + " no existe");
        }
        noteRepository.deleteById(id);
    }

    private Note.Status parseStatus(String raw) {
        try {
            return Note.Status.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("status inválido. Usa: OPEN, DONE");
        }
    }

}
