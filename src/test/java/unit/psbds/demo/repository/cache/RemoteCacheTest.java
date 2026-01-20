package unit.psbds.demo.repository.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import psbds.demo.repository.cache.RemoteCache;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;

class RemoteCacheTest {

    private static final String DATA_SOURCE_KEY = "TEST";
    private static final String TEST_KEY = "test-key";
    private static final String FORMATTED_KEY = "TEST:test-key";
    private static final long TEST_TTL = 60L;

    private RemoteCache remoteCache;
    private RedisDataSource mockDataSource;
    private ValueCommands<String, String> mockCommands;

    @BeforeEach
    void setUp() {
        mockDataSource = mock(RedisDataSource.class);
        mockCommands = mock(ValueCommands.class);
        
        when(mockDataSource.value(String.class)).thenReturn(mockCommands);
        
        remoteCache = new RemoteCache(DATA_SOURCE_KEY, mockDataSource);
    }

    @Test
    void set_when_keyAndValueProvided_should_delegateToSetnx() {
        // Arrange
        String value = "test-value";

        // Act
        remoteCache.set(TEST_KEY, value);

        // Assert
        verify(mockCommands).setnx(FORMATTED_KEY, value);
    }

    @Test
    void setex_when_keyValueAndDurationProvided_should_delegateToSetex() {
        // Arrange
        String value = "test-value";

        // Act
        remoteCache.setex(TEST_KEY, value, TEST_TTL);

        // Assert
        verify(mockCommands).setex(FORMATTED_KEY, TEST_TTL, value);
    }

    @Test
    void get_when_keyProvided_should_delegateToGetWithFormattedKey() {
        // Arrange
        String expectedValue = "cached-value";
        when(mockCommands.get(FORMATTED_KEY)).thenReturn(expectedValue);

        // Act
        String actualValue = remoteCache.get(TEST_KEY);

        // Assert
        assertEquals(expectedValue, actualValue, "Should return cached value");
        verify(mockCommands).get(FORMATTED_KEY);
    }

    @Test
    void del_when_keyProvided_should_delegateToGetdel() {
        // Act
        remoteCache.del(TEST_KEY);

        // Assert
        verify(mockCommands).getdel(FORMATTED_KEY);
    }

    @Test
    void getValue_when_validJsonInCache_should_deserializeAndReturnObject() throws Exception {
        // Arrange
        TestDto expectedDto = new TestDto("test-name", 123);
        ObjectMapper mapper = new ObjectMapper();
        String jsonValue = mapper.writeValueAsString(expectedDto);
        
        when(mockCommands.get(FORMATTED_KEY)).thenReturn(jsonValue);

        // Act
        TestDto actualDto = remoteCache.getValue(TEST_KEY, TestDto.class);

        // Assert
        assertNotNull(actualDto, "DTO should not be null");
        assertEquals(expectedDto.name, actualDto.name, "Name should match");
        assertEquals(expectedDto.value, actualDto.value, "Value should match");
        verify(mockCommands).get(FORMATTED_KEY);
    }

    @Test
    void getValue_when_keyNotInCache_should_returnNull() {
        // Arrange
        when(mockCommands.get(FORMATTED_KEY)).thenReturn(null);

        // Act
        TestDto actualDto = remoteCache.getValue(TEST_KEY, TestDto.class);

        // Assert
        assertNull(actualDto, "Should return null when key not in cache");
        verify(mockCommands).get(FORMATTED_KEY);
    }

    @Test
    void getValue_when_deserializationFails_should_returnNull() {
        // Arrange
        when(mockCommands.get(FORMATTED_KEY)).thenReturn("invalid-json");

        // Act
        TestDto actualDto = remoteCache.getValue(TEST_KEY, TestDto.class);

        // Assert
        assertNull(actualDto, "Should return null when deserialization fails");
        verify(mockCommands).get(FORMATTED_KEY);
    }

    @Test
    void setValue_when_objectProvided_should_serializeAndStoreWithTtl() throws Exception {
        // Arrange
        TestDto testDto = new TestDto("test-name", 123);
        ObjectMapper mapper = new ObjectMapper();
        String expectedJson = mapper.writeValueAsString(testDto);

        // Act
        remoteCache.setValue(TEST_KEY, testDto, TEST_TTL);

        // Assert
        verify(mockCommands).setex(eq(FORMATTED_KEY), eq(TEST_TTL), anyString());
    }

    // Helper DTO for testing
    private static class TestDto {
        public String name;
        public int value;

        public TestDto() {}

        public TestDto(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}
