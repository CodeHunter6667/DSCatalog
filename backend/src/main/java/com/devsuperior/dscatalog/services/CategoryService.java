package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(PageRequest pageRequest){
        Page<Category> page = repository.findAll(pageRequest);
        return page.map(CategoryDTO::new);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Category category = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada")
        );
        return new CategoryDTO(category);
    }
    @Transactional
    public CategoryDTO insert(CategoryDTO dto){
        Category entity = new Category();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto){
        try {
            Category entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);
            return new CategoryDTO(entity);
        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Categoria não encontrada");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Categoria não encontrada");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    public static void copyDtoToEntity(CategoryDTO dto, Category entity){
        entity.setId(dto.getId());
        entity.setName(dto.getName());
    }
}
