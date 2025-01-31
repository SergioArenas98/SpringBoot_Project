package com.example.SpringBoot_Pr03.controller;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.SpringBoot_Pr03.dao.NurseRepository;
import entity.Nurse;

@RestController
@RequestMapping("/nurse")
public class NurseController {

	private final NurseRepository nurseRepository;

	public NurseController(NurseRepository nurseRepository) {
		this.nurseRepository = nurseRepository;
	}

	@GetMapping("/index")
	public ResponseEntity<Iterable<Nurse>> getAllUsers() {
		Iterable<Nurse> nurses = nurseRepository.findAll();
		return ResponseEntity.ok(nurses);
	}

	@PostMapping("/login")
	public @ResponseBody ResponseEntity<Nurse> login(@RequestBody Nurse loginNurse) {
		// Get nurse by user's name
		Nurse nurse = nurseRepository.findByUser(loginNurse.getUser());

		// Check if nurse exists
		if (nurse != null) {
			// Check password
			if (nurse.getPassword().equals(loginNurse.getPassword())) {
				return ResponseEntity.ok(nurse);
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(nurse);
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<Nurse> findByName(@PathVariable String name) {

		Nurse nurse = nurseRepository.findByName(name);

		if (nurse != null && nurse.getName().equalsIgnoreCase(name)) {
			return ResponseEntity.ok(nurse);
		}

		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{nurseId}")
	public ResponseEntity<Nurse> findById(@PathVariable int nurseId) {
		Optional<Nurse> nurseOptional = nurseRepository.findById(nurseId);
		if (nurseOptional.isPresent()) {
			return ResponseEntity.ok(nurseOptional.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{nurseId}")
	public ResponseEntity<Nurse> update(@PathVariable int nurseId, @RequestBody Nurse nurse) {

		// Check if Nurse object is null
		if (nurse == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		// Search nurse by ID
		Optional<Nurse> existingNurse = nurseRepository.findById(nurseId);

		// Check if nurse ID exists
		if (existingNurse.isPresent()) {
			Nurse nursetoUpdate = existingNurse.get();

			// Update only not null data
			if (nurse.getName() != null) {
				nursetoUpdate.setName(nurse.getName());
			}
			if (nurse.getUser() != null) {
				nursetoUpdate.setUser(nurse.getUser());
			}
			if (nurse.getPassword() != null) {
				nursetoUpdate.setPassword(nurse.getPassword());
			}

			// Save changes
			Nurse updatedNurse = nurseRepository.save(nursetoUpdate);

			return new ResponseEntity<>(updatedNurse, HttpStatus.OK);
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}


	@PostMapping("/new")
	public @ResponseBody ResponseEntity<Nurse> createNurse(@RequestBody Nurse nurse) {
		String name = nurse.getName();
		String user = nurse.getUser();
		String password = nurse.getPassword();

		if (name == null || name.isEmpty() || password == null || password.isEmpty() || user == null
				|| user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}

		if (nurseRepository.findByName(name) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}

		Nurse newNurse = new Nurse();
		newNurse.setName(name);
		newNurse.setUser(user);
		newNurse.setPassword(password);

		Nurse savedNurse = nurseRepository.save(newNurse);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedNurse);
	}

	@DeleteMapping("/{nurseId}")
	public @ResponseBody ResponseEntity<Object> deleteNurse(@PathVariable int nurseId) {
		if (!nurseRepository.findById(nurseId).isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} else {
			nurseRepository.deleteById(nurseId);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

	}
}