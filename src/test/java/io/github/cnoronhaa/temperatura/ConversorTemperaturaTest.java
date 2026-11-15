package io.github.cnoronhaa.temperatura;

import org.junit.jupiter.api.Test;

import static io.github.cnoronhaa.temperatura.ConversorTemperatura.celsiusParaFahrenheit;
import static io.github.cnoronhaa.temperatura.ConversorTemperatura.fahrenheitParaCelsius;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversorTemperaturaTest {
    private static final double TOLERANCIA = 1e-9;

    @Test
    void convertePontoDeCongelamentoParaCelsius() {
        assertEquals(0.0, fahrenheitParaCelsius(32.0), TOLERANCIA);
    }

    @Test
    void converteTemperaturaFracionariaParaCelsius() {
        assertEquals(37.0, fahrenheitParaCelsius(98.6), TOLERANCIA);
    }

    @Test
    void converteTemperaturaNegativaParaFahrenheit() {
        assertEquals(-40.0, celsiusParaFahrenheit(-40.0), TOLERANCIA);
    }

    @Test
    void convertePontoDeEbulicaoParaFahrenheit() {
        assertEquals(212.0, celsiusParaFahrenheit(100.0), TOLERANCIA);
    }
}
