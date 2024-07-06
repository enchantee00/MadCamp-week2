CREATE TABLE `users` (
  `id` integer PRIMARY KEY,
  `username` varchar(255),
  `password` varchar(255),
  `role` varchar(255),
  `play_count` integer,
  `best_score` integer,
  `point` integer,
  `item_count` integer,
  `item_slow_down` integer,
  `item_no_bomb` integer,
  `item_big_size` integer,
  `item_triple_points` integer,
  `total_duaration` integer,
  `created_at` timestamp
);

CREATE TABLE `event_item_slow_down` (
  `id` integer,
  `turn_id` integer,
  `user_id` integer,
  `pressed_ts` timestamp,
  PRIMARY KEY (`id`, `turn_id`)
);

CREATE TABLE `event_item_no_bomb` (
  `id` integer,
  `turn_id` integer,
  `user_id` integer,
  `pressed_ts` timestamp,
  PRIMARY KEY (`id`, `turn_id`)
);

CREATE TABLE `event_item_big_size` (
  `id` integer,
  `turn_id` integer,
  `user_id` integer,
  `pressed_ts` timestamp,
  PRIMARY KEY (`id`, `turn_id`)
);

CREATE TABLE `event_item_triple_points` (
  `id` integer,
  `turn_id` integer,
  `user_id` integer,
  `pressed_ts` timestamp,
  PRIMARY KEY (`id`, `turn_id`)
);

CREATE TABLE `event_turn` (
  `id` integer PRIMARY KEY,
  `user_id` integer,
  `turn_duration` timestamp
);

ALTER TABLE `event_item_slow_down` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `event_item_no_bomb` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `event_item_big_size` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `event_item_triple_points` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `event_turn` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `event_item_slow_down` ADD FOREIGN KEY (`turn_id`) REFERENCES `event_turn` (`id`);

ALTER TABLE `event_item_big_size` ADD FOREIGN KEY (`turn_id`) REFERENCES `event_turn` (`id`);

ALTER TABLE `event_item_no_bomb` ADD FOREIGN KEY (`turn_id`) REFERENCES `event_turn` (`id`);

ALTER TABLE `event_item_triple_points` ADD FOREIGN KEY (`turn_id`) REFERENCES `event_turn` (`id`);
