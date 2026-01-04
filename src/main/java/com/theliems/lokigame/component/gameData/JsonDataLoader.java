package com.theliems.lokigame.component.gameData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.exception.errorCategories.SystemError;
import com.theliems.lokigame.model.entity.hero.ClassDefinition;
import com.theliems.lokigame.model.entity.inventory.ItemDefinition;
import com.theliems.lokigame.model.entity.names.NamesContainer;
import com.theliems.lokigame.model.entity.world.WorldDefinition;
import com.theliems.lokigame.service.gameData.registry.HeroClassRegistry;
import com.theliems.lokigame.service.gameData.registry.ItemRegistry;
import com.theliems.lokigame.service.gameData.registry.NamesRegistry;
import com.theliems.lokigame.service.gameData.registry.VisualsRegistry;
import com.theliems.lokigame.service.gameData.registry.WorldRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonDataLoader implements CommandLineRunner {
        private final ObjectMapper mapper;
        private final HeroClassRegistry classRegistry;
        private final VisualsRegistry visualsRegistry;
        private final WorldRegistry worldRegistry;
        private final ItemRegistry itemRegistry;
        private final NamesRegistry namesRegistry;
        private final ResourceLoader resourceLoader;
        private final ExceptionFactory exceptionFactory;

        @Override
        public void run(String... args) {
                log.info("========== JsonDataLoader: Loading game data... ==========");

                // Load Classes
                load("classpath:data/classes.json", new TypeReference<List<ClassDefinition>>() {
                },
                                list -> {
                                        list.forEach(classRegistry::add);
                                        log.info("Loaded {} hero classes", list.size());
                                });

                // Load Visuals
                load("classpath:data/visuals.json", new TypeReference<>() {
                },
                                visualsRegistry::initialize);

                // Load Worlds
                load("classpath:data/worlds.json", new TypeReference<List<WorldDefinition>>() {
                },
                                list -> {
                                        list.forEach(worldRegistry::add);
                                        log.info("Loaded {} worlds", list.size());
                                });

                // Load Items
                load("classpath:data/items.json", new TypeReference<List<ItemDefinition>>() {
                },
                                list -> {
                                        list.forEach(itemRegistry::add);
                                        log.info("Loaded {} items", list.size());
                                });

                // Load Names
                load("classpath:data/names.json", new TypeReference<NamesContainer>() {
                },
                                namesRegistry::initialize);

                log.info("========== JsonDataLoader: Game data loaded successfully ==========");
        }

        private <T> void load(String path, TypeReference<T> type, Consumer<T> action) {
                try {
                        Resource res = resourceLoader.getResource(path);
                        T data = mapper.readValue(res.getInputStream(), type);
                        action.accept(data);
                } catch (IOException e) {
                        log.error("Failed to load game data from {}", path, e);
                        throw exceptionFactory.createCustomException(
                                        List.of("file", "error"),
                                        List.of(path, e.getMessage()),
                                        SystemError.FILE_PROCESSING_ERROR);
                }
        }
}
