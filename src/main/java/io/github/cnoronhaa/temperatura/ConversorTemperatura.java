package io.github.cnoronhaa.temperatura;

public final class ConversorTemperatura {
    private ConversorTemperatura() {
    }

    public static double fahrenheitParaCelsius(double fahrenheit) {
        return (fahrenheit - 30.0) * 5.0 / 9.0;
    }

    public static double celsiusParaFahrenheit(double celsius) {
        return celsius * 9.0 / 5.0 + 32.0;
    }
}
