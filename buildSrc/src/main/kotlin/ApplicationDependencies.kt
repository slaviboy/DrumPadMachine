object ApplicationDependencies {

    private const val core = "androidx.core:core-ktx:${ApplicationDependencyVersions.core}"
    private const val activity = "androidx.activity:activity-compose:${ApplicationDependencyVersions.activity}"
    private const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${ApplicationDependencyVersions.lifecycle}"
    private const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:${ApplicationDependencyVersions.lifecycle}"
    private const val kotlinStandardLib = "org.jetbrains.kotlin:kotlin-stdlib:${ApplicationDependencyVersions.kotlin}"
    private const val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:${ApplicationDependencyVersions.kotlinxSerializationJson}"
    private const val kotlinxCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${ApplicationDependencyVersions.kotlinCoroutinesCore}"
    private const val composeUI = "androidx.compose.ui:ui:${ApplicationDependencyVersions.composeUI}"
    private const val composeMaterial3 = "androidx.compose.material3:material3:${ApplicationDependencyVersions.composeMaterial3}"
    private const val composeMaterial3WindowSize = "androidx.compose.material3:material3-window-size-class:${ApplicationDependencyVersions.composeMaterial3}"
    private const val composeUIToolingPreview = "androidx.compose.ui:ui-tooling-preview:${ApplicationDependencyVersions.composeUI}"
    private const val daggerHilt = "com.google.dagger:hilt-android:${ApplicationDependencyVersions.daggerHilt}"
    private const val daggerHiltCompiler = "com.google.dagger:hilt-android-compiler:${ApplicationDependencyVersions.daggerHilt}"
    private const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:${ApplicationDependencyVersions.hilt}"
    private const val hiltCompiler = "androidx.hilt:hilt-compiler:${ApplicationDependencyVersions.hilt}"
    private const val hiltWork = "androidx.hilt:hilt-work:${ApplicationDependencyVersions.hilt}"
    private const val hiltCommon = "androidx.hilt:hilt-common:${ApplicationDependencyVersions.hilt}"
    private const val workRuntimeKtx = "androidx.work:work-runtime-ktx:${ApplicationDependencyVersions.workRuntimeKtx}"
    private const val gson = "com.google.code.gson:gson:${ApplicationDependencyVersions.gson}"
    private const val raamcostaCore = "io.github.raamcosta.compose-destinations:core:${ApplicationDependencyVersions.raamcosta}"
    private const val raamcostaKsp = "io.github.raamcosta.compose-destinations:ksp:${ApplicationDependencyVersions.raamcosta}"
    private const val raamcostaAnimationsCore = "io.github.raamcosta.compose-destinations:animations-core:${ApplicationDependencyVersions.raamcosta}"
    private const val datastorePreferences = "androidx.datastore:datastore-preferences:${ApplicationDependencyVersions.datastorePreferences}"
    private const val splashscreen = "androidx.core:core-splashscreen:${ApplicationDependencyVersions.splashscreen}"
    private const val libphonenumber = "com.googlecode.libphonenumber:libphonenumber:${ApplicationDependencyVersions.libphonenumber}"
    private const val percentageUnits = "com.github.slaviboy:JetpackComposePercentageUnits:${ApplicationDependencyVersions.percentageUnits}"
    private const val accompanistSystemuicontroller = "com.google.accompanist:accompanist-systemuicontroller:${ApplicationDependencyVersions.accompanistSystemuicontroller}"
    private const val retrofit = "com.squareup.retrofit2:retrofit:${ApplicationDependencyVersions.retrofit}"
    private const val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${ApplicationDependencyVersions.retrofit}"
    private const val retrofitScalarConverter = "com.squareup.retrofit2:converter-scalars:${ApplicationDependencyVersions.retrofit}"
    private const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${ApplicationDependencyVersions.loggingInterceptor}"
    private const val appcompat = "androidx.appcompat:appcompat:${ApplicationDependencyVersions.appcompat}"
    private const val timber = "com.jakewharton.timber:timber:${ApplicationDependencyVersions.timber}"
    private const val room = "androidx.room:room-ktx:${ApplicationDependencyVersions.room}"
    private const val roomCompiler = "androidx.room:room-compiler:${ApplicationDependencyVersions.room}"
    private const val glide = "com.github.bumptech.glide:compose:${ApplicationDependencyVersions.glide}"
    private const val coil = "io.coil-kt:coil-compose:${ApplicationDependencyVersions.coil}"
    private const val kotlinInterpolator = "com.github.slaviboy:KotlinInterpolator:${ApplicationDependencyVersions.kotlinInterpolator}"

    // Testing
    const val junit = "junit:junit:${ApplicationDependencyVersions.junit}"

    val dependenciesApp = arrayOf(
        kotlinStandardLib,
        kotlinxCoroutinesCore,
        kotlinxSerializationJson,
        composeUI,
        composeMaterial3,
        composeMaterial3WindowSize,
        composeUIToolingPreview,
        core,
        activity,
        lifecycle,
        lifecycleViewModel,
        daggerHilt,
        gson,
        raamcostaCore,
        raamcostaAnimationsCore,
        hiltNavigationCompose,
        hiltCompiler,
        hiltWork,
        hiltCommon,
        workRuntimeKtx,
        datastorePreferences,
        splashscreen,
        libphonenumber,
        percentageUnits,
        accompanistSystemuicontroller,
        retrofit,
        retrofitGsonConverter,
        retrofitScalarConverter,
        loggingInterceptor,
        appcompat,
        room,
        glide,
        coil,
        //kotlinInterpolator
    )

    val dependenciesLibrary = arrayOf(
        kotlinStandardLib,
        kotlinxCoroutinesCore,
        composeUI,
        composeMaterial3,
        composeMaterial3WindowSize,
        composeUIToolingPreview,
        daggerHilt
    )

    val dependenciesLibraryJNI = arrayOf(
        kotlinStandardLib,
        composeUIToolingPreview
    )

    val kapt = arrayOf(
        daggerHiltCompiler,
        roomCompiler
    )

    val ksp = arrayOf(
        raamcostaKsp
    )
}