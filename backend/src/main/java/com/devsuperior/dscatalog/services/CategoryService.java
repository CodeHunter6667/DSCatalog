package com.devsuperior.dscatalog.services;

import java.util.List;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;
import jakarta.transaction.TransactionScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;


@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        List<Category> list = repository.findAll();
        return list.stream().map(CategoryDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Category category = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Categoria não encontrada")
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
            throw new EntityNotFoundException("Categoria não encontrada");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new EntityNotFoundException("Categoria não encontrada");
        }
        repository.deleteById(id);
    }

    public static void copyDtoToEntity(CategoryDTO dto, Category entity){
        entity.setId(dto.getId());
        entity.setName(dto.getName());
    }
}
