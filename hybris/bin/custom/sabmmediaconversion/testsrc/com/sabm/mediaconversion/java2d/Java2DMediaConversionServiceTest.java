/*
 * package com.sabm.mediaconversion.java2d;
 *
 * import com.sabm.mediaconversion.java2d.actions.ConversionAction; import
 * com.sabm.mediaconversion.java2d.actions.handler.ActionHandler; import
 * com.sabm.mediaconversion.java2d.actions.impl.ResizeConversionAction; import
 * com.sabm.mediaconversion.java2d.actions.util.ActionRegistry; import
 * com.sabm.mediaconversion.java2d.actions.util.OptionsProcessor; import de.hybris.bootstrap.annotations.UnitTest;
 * import org.apache.commons.cli.CommandLine; import org.apache.commons.cli.CommandLineParser; import
 * org.apache.commons.cli.Options; import org.apache.commons.cli.ParseException; import org.junit.Before; import
 * org.junit.Test; import org.junit.runner.RunWith; import org.mockito.InjectMocks; import org.mockito.Mock; import
 * org.mockito.Mockito; import org.mockito.MockitoAnnotations; import org.powermock.api.mockito.PowerMockito; import
 * org.powermock.core.classloader.annotations.PrepareForTest; //import org.powermock.modules.junit4.PowerMockRunner;
 *
 * import javax.imageio.ImageIO; import java.awt.*; import java.awt.image.BufferedImage; import java.io.FileInputStream;
 * import java.io.FileOutputStream; import java.io.IOException; import java.util.Arrays; import java.util.Collections;
 * import java.util.List;
 *
 * @UnitTest
 *
 * @RunWith(PowerMockRunner.class)
 *
 * @PrepareForTest({Java2DMediaConversionService.class,ImageIO.class,BufferedImage.class}) public class
 * Java2DMediaConversionServiceTest {
 *
 * @InjectMocks private Java2DMediaConversionService java2DMediaConversionService;
 *
 * @Mock private CommandLineParser commandLineParser;
 *
 * @Mock private OptionsProcessor optionsProcessor;
 *
 * @Mock private ActionRegistry actionRegistry;
 *
 * @Before public void setUp(){ java2DMediaConversionService = new Java2DMediaConversionService();
 * MockitoAnnotations.initMocks(this); }
 *
 * @Test(expected = NullPointerException.class) public void shouldThrowNPEOnConvert() throws IOException {
 * java2DMediaConversionService.convert(null); }
 *
 * @Test(expected = RuntimeException.class) public void shouldThrowREGivenInvalidListOnConvert() throws IOException,
 * ParseException {
 *
 * final Options options = Mockito.mock(Options.class); final List<String> emptyList = Mockito.mock(List.class); final
 * String listArr[] = new String[0]; final CommandLine commandLine = Mockito.mock(CommandLine.class);
 *
 * Mockito.when(optionsProcessor.provideOptions()).thenReturn(options);
 * Mockito.when(emptyList.toArray(Mockito.any(String[].class))).thenReturn(listArr);
 * Mockito.when(commandLineParser.parse(options,listArr)).thenReturn(commandLine);
 * Mockito.when(optionsProcessor.extractActions(commandLine)).thenReturn(Collections.emptyList());
 * java2DMediaConversionService.convert(emptyList); }
 *
 * @Test(expected = RuntimeException.class) public void shouldThrowREGivenActionsNotSupportedOnCovert() throws
 * ParseException, IOException {
 *
 * final Options options = Mockito.mock(Options.class); final List<String> mockList = Mockito.mock(List.class); final
 * String listArr[] = new String[0]; final CommandLine commandLine = Mockito.mock(CommandLine.class); final
 * List<ConversionAction> conversionActions = Mockito.mock(List.class);
 *
 * Mockito.when(conversionActions.isEmpty()).thenReturn(false);
 * Mockito.when(mockList.toArray(Mockito.any(String[].class))).thenReturn(listArr);
 * Mockito.when(optionsProcessor.provideOptions()).thenReturn(options);
 * Mockito.when(commandLineParser.parse(options,listArr)).thenReturn(commandLine);
 * Mockito.when(optionsProcessor.extractActions(commandLine)).thenReturn(conversionActions);
 * Mockito.when(commandLine.getArgs()).thenReturn(null);
 *
 * java2DMediaConversionService.convert(mockList); }
 *
 * @Test public void shouldConvertGivenValidInputs() throws Exception {
 *
 * final Options options = Mockito.mock(Options.class); final List<String> mockList = Mockito.mock(List.class); final
 * String listArr[] = new String[2]; final CommandLine commandLine = Mockito.mock(CommandLine.class);
 *
 * final String[] cmdArgs = new String[]{"input1","input2"}; final FileInputStream inputMocked =
 * Mockito.mock(FileInputStream.class); final ConversionAction mockedConversionAction =
 * Mockito.mock(ConversionAction.class); final ConversionAction redrawRequiredConversionAction =
 * Mockito.mock(ResizeConversionAction.class); final List<ConversionAction> conversionActions =
 * Arrays.asList(redrawRequiredConversionAction,mockedConversionAction); final ActionHandler actionHandler =
 * Mockito.mock(ActionHandler.class); final ActionHandler requiresRedrawHandler = Mockito.mock(ActionHandler.class);
 * final BufferedImage bufferedImage = Mockito.mock(BufferedImage.class); final BufferedImage redrawBufferedImage =
 * Mockito.mock(BufferedImage.class); final FileOutputStream mockedOutput = Mockito.mock(FileOutputStream.class); final
 * Image redrawRequiredImage = Mockito.mock(Image.class);
 *
 * //PowerMockito.mockStatic(ImageIO.class);
 * Mockito.when(mockList.toArray(Mockito.any(String[].class))).thenReturn(listArr);
 * Mockito.when(optionsProcessor.provideOptions()).thenReturn(options);
 * Mockito.when(commandLineParser.parse(options,listArr)).thenReturn(commandLine);
 * Mockito.when(optionsProcessor.extractActions(commandLine)).thenReturn(conversionActions);
 * Mockito.when(commandLine.getArgs()).thenReturn(cmdArgs); //
 * PowerMockito.whenNew(FileInputStream.class).withArguments(cmdArgs[0]).thenReturn(inputMocked);
 * //PowerMockito.when(ImageIO.read(inputMocked)).thenReturn(bufferedImage);
 * Mockito.when(actionRegistry.getActionHandlerForConversionClass(Mockito.eq(mockedConversionAction.getClass()))).
 * thenReturn(actionHandler);
 * Mockito.when(actionRegistry.getActionHandlerForConversionClass(Mockito.eq(redrawRequiredConversionAction.getClass()))
 * ).thenReturn(requiresRedrawHandler);
 * Mockito.when(requiresRedrawHandler.convert(bufferedImage,redrawRequiredConversionAction)).thenReturn(
 * redrawRequiredImage);
 * Mockito.when(actionHandler.convert(redrawBufferedImage,mockedConversionAction)).thenReturn(redrawBufferedImage);
 * PowerMockito.whenNew(BufferedImage.class).withAnyArguments().thenReturn(redrawBufferedImage);
 * Mockito.when(redrawBufferedImage.createGraphics()).thenReturn(Mockito.mock(Graphics2D.class));
 * PowerMockito.whenNew(FileOutputStream.class).withArguments(Mockito.anyString()).thenReturn(mockedOutput);
 * PowerMockito.when(ImageIO.write(Mockito.eq(redrawBufferedImage),Mockito.any(),Mockito.eq(mockedOutput))).thenReturn(
 * false);
 *
 * java2DMediaConversionService.convert(mockList); }
 *
 * @Test(expected = IOException.class) public void shouldThrowIOExceptionOnInvalidInput() throws Exception {
 *
 * final Options options = Mockito.mock(Options.class); final List<String> mockList = Mockito.mock(List.class); final
 * String listArr[] = new String[2]; final CommandLine commandLine = Mockito.mock(CommandLine.class);
 *
 * final String[] cmdArgs = new String[]{"input1","input2"}; final FileInputStream inputMocked =
 * Mockito.mock(FileInputStream.class); final ConversionAction mockedConversionAction =
 * Mockito.mock(ConversionAction.class); final ConversionAction redrawRequiredConversionAction =
 * Mockito.mock(ResizeConversionAction.class); final List<ConversionAction> conversionActions =
 * Arrays.asList(redrawRequiredConversionAction,mockedConversionAction); final ActionHandler actionHandler =
 * Mockito.mock(ActionHandler.class); final ActionHandler requiresRedrawHandler = Mockito.mock(ActionHandler.class);
 * final BufferedImage bufferedImage = Mockito.mock(BufferedImage.class); final BufferedImage redrawBufferedImage =
 * Mockito.mock(BufferedImage.class); final FileOutputStream mockedOutput = Mockito.mock(FileOutputStream.class); final
 * Image redrawRequiredImage = Mockito.mock(Image.class);
 *
 * // PowerMockito.mockStatic(ImageIO.class);
 * Mockito.when(mockList.toArray(Mockito.any(String[].class))).thenReturn(listArr);
 * Mockito.when(optionsProcessor.provideOptions()).thenReturn(options);
 * Mockito.when(commandLineParser.parse(options,listArr)).thenReturn(commandLine);
 * Mockito.when(optionsProcessor.extractActions(commandLine)).thenReturn(conversionActions);
 * Mockito.when(commandLine.getArgs()).thenReturn(cmdArgs);
 * //PowerMockito.whenNew(FileInputStream.class).withArguments(cmdArgs[0]).thenReturn(inputMocked);
 * //PowerMockito.when(ImageIO.read(inputMocked)).thenThrow(new IOException());
 * Mockito.when(actionRegistry.getActionHandlerForConversionClass(Mockito.eq(mockedConversionAction.getClass()))).
 * thenReturn(actionHandler);
 * Mockito.when(actionRegistry.getActionHandlerForConversionClass(Mockito.eq(redrawRequiredConversionAction.getClass()))
 * ).thenReturn(requiresRedrawHandler);
 * Mockito.when(requiresRedrawHandler.convert(bufferedImage,redrawRequiredConversionAction)).thenReturn(
 * redrawRequiredImage);
 * Mockito.when(actionHandler.convert(redrawBufferedImage,mockedConversionAction)).thenReturn(redrawBufferedImage);
 * //PowerMockito.whenNew(BufferedImage.class).withAnyArguments().thenReturn(redrawBufferedImage);
 * Mockito.when(redrawBufferedImage.createGraphics()).thenReturn(Mockito.mock(Graphics2D.class)); //
 * PowerMockito.whenNew(FileOutputStream.class).withArguments(Mockito.anyString()).thenReturn(mockedOutput);
 * //PowerMockito.when(ImageIO.write(Mockito.eq(redrawBufferedImage),Mockito.any(),Mockito.eq(mockedOutput))).thenReturn
 * (false);
 *
 * java2DMediaConversionService.convert(mockList); }
 *
 * @Test(expected = IOException.class) public void shouldThrowIOExceptionOnInvalidOutput() throws Exception {
 *
 * final Options options = Mockito.mock(Options.class); final List<String> mockList = Mockito.mock(List.class); final
 * String listArr[] = new String[2]; final CommandLine commandLine = Mockito.mock(CommandLine.class);
 *
 * final String[] cmdArgs = new String[]{"input1","input2"}; final FileInputStream inputMocked =
 * Mockito.mock(FileInputStream.class); final ConversionAction mockedConversionAction =
 * Mockito.mock(ConversionAction.class); final ConversionAction redrawRequiredConversionAction =
 * Mockito.mock(ResizeConversionAction.class); final List<ConversionAction> conversionActions =
 * Arrays.asList(redrawRequiredConversionAction,mockedConversionAction); final ActionHandler actionHandler =
 * Mockito.mock(ActionHandler.class); final ActionHandler requiresRedrawHandler = Mockito.mock(ActionHandler.class);
 * final BufferedImage bufferedImage = Mockito.mock(BufferedImage.class); final BufferedImage redrawBufferedImage =
 * Mockito.mock(BufferedImage.class); final FileOutputStream mockedOutput = Mockito.mock(FileOutputStream.class); final
 * Image redrawRequiredImage = Mockito.mock(Image.class);
 *
 * // PowerMockito.mockStatic(ImageIO.class);
 * Mockito.when(mockList.toArray(Mockito.any(String[].class))).thenReturn(listArr);
 * Mockito.when(optionsProcessor.provideOptions()).thenReturn(options);
 * Mockito.when(commandLineParser.parse(options,listArr)).thenReturn(commandLine);
 * Mockito.when(optionsProcessor.extractActions(commandLine)).thenReturn(conversionActions);
 * Mockito.when(commandLine.getArgs()).thenReturn(cmdArgs); //
 * PowerMockito.whenNew(FileInputStream.class).withArguments(cmdArgs[0]).thenReturn(inputMocked); //
 * PowerMockito.when(ImageIO.read(inputMocked)).thenReturn(bufferedImage);
 * Mockito.when(actionRegistry.getActionHandlerForConversionClass(Mockito.eq(mockedConversionAction.getClass()))).
 * thenReturn(actionHandler);
 * Mockito.when(actionRegistry.getActionHandlerForConversionClass(Mockito.eq(redrawRequiredConversionAction.getClass()))
 * ).thenReturn(requiresRedrawHandler);
 * Mockito.when(requiresRedrawHandler.convert(bufferedImage,redrawRequiredConversionAction)).thenReturn(
 * redrawRequiredImage);
 * Mockito.when(actionHandler.convert(redrawBufferedImage,mockedConversionAction)).thenReturn(redrawBufferedImage);
 * //PowerMockito.whenNew(BufferedImage.class).withAnyArguments().thenReturn(redrawBufferedImage);
 * Mockito.when(redrawBufferedImage.createGraphics()).thenReturn(Mockito.mock(Graphics2D.class));
 * //PowerMockito.whenNew(FileOutputStream.class).withArguments(Mockito.anyString()).thenReturn(mockedOutput);
 * //PowerMockito.when(ImageIO.write(Mockito.eq(redrawBufferedImage),Mockito.any(),Mockito.eq(mockedOutput))).thenThrow(
 * new IOException());
 *
 * java2DMediaConversionService.convert(mockList); }
 *
 * @Test(expected = UnsupportedOperationException.class) public void shouldThrowUnsupportedExceptionOnIdentify()throws
 * IOException{ java2DMediaConversionService.identify(null); }
 *
 * }
 */