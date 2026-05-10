package edu.cit.belen.pantrypulse.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryLogRepository inventoryLogRepository;

    @InjectMocks
    private InventoryLogService inventoryLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInventory_ReturnsList() {
        InventoryLog item = new InventoryLog();
        item.setItemName("Rice");
        item.setQuantity(10);
        when(inventoryLogRepository.findAll()).thenReturn(List.of(item));

        List<InventoryLog> result = inventoryLogService.getAllInventory();
        assertFalse(result.isEmpty());
        assertEquals("Rice", result.get(0).getItemName());
    }

    @Test
    void testAddInventory_SavesItem() {
        InventoryLog item = new InventoryLog();
        item.setItemName("Rice");
        item.setQuantity(10);
        when(inventoryLogRepository.save(any(InventoryLog.class))).thenReturn(item);

        InventoryLog saved = inventoryLogService.addInventory(item);
        assertEquals("Rice", saved.getItemName());
        verify(inventoryLogRepository, times(1)).save(item);
    }

    @Test
    void testDeleteInventory_DeletesById() {
        doNothing().when(inventoryLogRepository).deleteById(1L);
        inventoryLogService.deleteInventory(1L);
        verify(inventoryLogRepository, times(1)).deleteById(1L);
    }
}
