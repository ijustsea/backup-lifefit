package com.kh.lifeFit.repository.SleepRepository;

import com.kh.lifeFit.domain.sleepManager.SleepHabit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SleepHabitRepository extends JpaRepository<SleepHabit, Long> {

}
