# Incidente de seguridad: clave de Google API expuesta

Fecha detectada: 2026-02-17

Resumen

- Se detectó una clave de API de Google expuesta en artefactos del perfil de Chrome y en el historial Git del repositorio.
- Clave expuesta (redactada aquí): `REDACTED-API-KEY`.

Acciones realizadas

- Busqueda en el workspace y en commits: se localizó la clave en commits históricos.
- Se limpió el historial del repositorio usando `git-filter-repo` sobre un mirror y se aplicó `git push --force --mirror` al remoto.
- Se eliminaron refs originales (`refs/original`) y se ejecutó `git gc` en el mirror.
- Se añadieron exclusiones a `.gitignore` para evitar subir perfiles y caches locales; se creó `scripts/remove-secret.ps1` para ayudar en la limpieza.
- Se cambió el código para no usar claves hardcodeadas: `Config.java` ahora lee `System.getenv("GOOGLE_API_KEY")`.
- Se limpiaron archivos de cache/locales detectados (ej. `*.ldb`, `data_*`).

Acciones requeridas (pendientes)

1. Revocar la API key expuesta en Google Cloud Console inmediatamente.
2. Crear una nueva API key con restricciones adecuadas (HTTP referrers, IPs, restringir APIs).
3. Revisar logs de uso de la key en Google Cloud para detectar accesos no autorizados y notificar al equipo de seguridad si se detecta actividad sospechosa.
4. Comunicar a todos los colaboradores que reclonen el repositorio (NO hacer pull) porque el historial fue reescrito.
5. Cambiar en los despliegues/CI cualquier variable que usara la clave antigua por la nueva.

Instrucciones para desarrolladores

- Reclonar el repositorio:

```
git clone https://github.com/JordyChamba/AutomatizacionSRI_JAVA.git
```

- No restaurar copias locales antiguas que contengan la clave. Si tienes claves en variables de entorno locales, reemplázalas por la nueva clave.

Contacto

- Reporta dudas o sospechas a: Jordy Chamba <jordy_chamba@hotmail.com>

Notas

- Esta documentación recoge las acciones técnicas realizadas hasta ahora y los pasos siguientes recomendados. Confirma cuando la clave haya sido revocada para cerrar el incidente.
