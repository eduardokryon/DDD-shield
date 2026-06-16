# DDDLock ProGuard Rules
# Keep CallScreeningService
-keep class com.dddlock.service.DDDCallScreeningService { *; }

# Keep data classes
-keep class com.dddlock.model.** { *; }
-keep class com.dddlock.domain.model.** { *; }
