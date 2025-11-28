package aaditya.com.moneymanager.service;

import aaditya.com.moneymanager.dto.CategoryDTO;
import aaditya.com.moneymanager.entity.CategoryEntity;
import aaditya.com.moneymanager.entity.ProfileEntity;
import aaditya.com.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // save category
    public CategoryDTO saveCategory(CategoryDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(dto.getName(), profile.getId())) {
            throw new RuntimeException("Category with this name already exists");
        }

        CategoryEntity newCategory = toEntity(dto, profile);
        newCategory = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    // get categories for current user
    public List<CategoryDTO> getCategoriesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    // get categories by tpe for current user
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> entities = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    // update categories
    public CategoryDTO updateCategories(Long categoryId, CategoryDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or not accessible"));

        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory = categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }

    // helper method;
    private CategoryEntity toEntity(CategoryDTO dto, ProfileEntity profile) {
        return CategoryEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .type(dto.getType())
                .profile(profile)
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() != null ? entity.getProfile().getId() : null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdateAt())
                .build();
    }
}
