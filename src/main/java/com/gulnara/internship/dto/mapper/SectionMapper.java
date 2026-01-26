package com.gulnara.internship.dto.mapper;

import com.gulnara.internship.dto.SectionDto;
import com.gulnara.internship.model.Section;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting Section entities to SectionDto
 */
public final class SectionMapper {

    private SectionMapper() {
        // Utility class, no instances allowed
    }

    public static SectionDto toDto(Section section) {
        if (section == null) {
            return null;
        }
        return new SectionDto(
                section.getId(),
                section.getContent()
        );
    }
    public static List<SectionDto> toDtoList(List<Section> sections) {
        return sections.stream()
                .map(SectionMapper::toDto)
                .collect(Collectors.toList());
    }
}
