package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.model.Table;
import com.pgbezerra.bezerras.repository.TableRepository;
import com.pgbezerra.bezerras.services.TableService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TableServiceImpl implements TableService {
	
	private static final Logger LOG = Logger.getLogger(TableServiceImpl.class);

	@Autowired
	private final TableRepository tableRepository;
	
	public TableServiceImpl(final TableRepository tableRepository) {
		this.tableRepository = tableRepository;
	}

	@Override
	public Table insert(Table obj) {
		obj.setId(null);
		return tableRepository.insert(obj);
	}

	@Override
	public Boolean update(Table obj) {
		Table oldObj = findById(obj.getId());
		updateDate(oldObj, obj);
		Boolean updated = tableRepository.update(oldObj);
		LOG.info(String.format("Table %s updated: %s", obj, updated));
		return updated;
	}

	private void updateDate(Table oldObj, Table obj) {
		oldObj.setName(obj.getName());
	}

	@Override
	public List<Table> findAll() {
		List<Table> tables = tableRepository.findAll();
		LOG.info(String.format("%s tables found", tables.size()));
		if(!tables.isEmpty()) 
			return tables;
		throw new ResourceNotFoundException("No tables found");
	}

	@Override
	public Table findById(Integer id) {
		Optional<Table> table = tableRepository.findById(id);
		LOG.info(String.format("Table with id %s found: %s", id, table.isPresent()));
		if(table.isPresent())
			return table.get();
		throw new ResourceNotFoundException(String.format("No tables found with id: %s", id));
	}

	@Override
	public Boolean deleteById(Integer id) {
		findById(id);
		Boolean deleted = tableRepository.deleteById(id);
		LOG.info(String.format("Table %s deleted: %s", id, deleted));
		return deleted;
	}

}
