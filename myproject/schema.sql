CREATE TABLE `users` (
  `id` integer AUTO_INCREMENT PRIMARY KEY NOT NULL,
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
  `total_duration` timestamp,
  `created_at` timestamp
);

CREATE TABLE `event_item_slow_down` (
  `id` integer AUTO_INCREMENT PRIMARY KEY NOT NULL,
  `turn_id` integer NOT NULL,
  `user_id` integer NOT NULL,
  `pressed_ts` timestamp
);

CREATE TABLE `event_item_no_bomb` (
  `id` integer AUTO_INCREMENT PRIMARY KEY NOT NULL,
  `turn_id` integer NOT NULL,
  `user_id` integer NOT NULL,
  `pressed_ts` timestamp
);

CREATE TABLE `event_item_big_size` (
  `id` integer AUTO_INCREMENT PRIMARY KEY NOT NULL,
  `turn_id` integer NOT NULL,
  `user_id` integer NOT NULL,
  `pressed_ts` timestamp
);

CREATE TABLE `event_item_triple_points` (
  `id` integer AUTO_INCREMENT PRIMARY KEY NOT NULL,
  `turn_id` integer NOT NULL,
  `user_id` integer NOT NULL,
  `pressed_ts` timestamp
);

CREATE TABLE `event_turn` (
  `id` integer AUTO_INCREMENT PRIMARY KEY NOT NULL,
  `user_id` integer NOT NULL,
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
