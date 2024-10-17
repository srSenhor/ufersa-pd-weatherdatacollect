package br.edu.ufersa.pd.weatherdatacollect.database;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import br.edu.ufersa.pd.weatherdatacollect.entities.WeatherData;

public class PseudoDatabase {

    private static PseudoDatabase instance;
    private static ConcurrentHashMap<Long, WeatherData> db;
    private static ReadWriteLock lock;
    private static long id;

    private PseudoDatabase() {
        db = new ConcurrentHashMap<>();
        lock = new ReentrantReadWriteLock();
        id = 0;
    }

    public static PseudoDatabase getInstance() {
        
        if (instance == null) { instance = new PseudoDatabase(); }
        return instance;

    }
    
    public void insert(WeatherData data) {
        
        Lock wLock = lock.writeLock();

        wLock.lock();
        db.put(id++, data);
        wLock.unlock();

    }

    public ConcurrentHashMap<Long, WeatherData> get() {
        return db;
    }

    public WeatherData get(long idData) {
        
        Lock rLock = lock.readLock();
        WeatherData data = null;

        rLock.lock();
        data = db.get(idData);
        rLock.unlock();

        return data;
        
    }

}