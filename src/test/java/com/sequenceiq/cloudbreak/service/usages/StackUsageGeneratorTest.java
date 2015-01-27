package com.sequenceiq.cloudbreak.service.usages;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.repository.CloudbreakEventRepository;
import com.sequenceiq.cloudbreak.repository.CloudbreakUsageRepository;

public class StackUsageGeneratorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCloudbreakUsageGeneratorServiceTest.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @InjectMocks
    private StackUsageGenerator underTest;
    @Mock
    private CloudbreakUsageRepository usageRepository;
    @Mock
    private CloudbreakEventRepository eventRepository;

    private Calendar referenceCalendar;
    private IntervalStackUsageGenerator iSUG;

    @Before
    public void before() {
        String referenceDateStr = "2014-09-24";
        try {
            referenceCalendar = Calendar.getInstance();
            referenceCalendar.setTime(DATE_FORMAT.parse(referenceDateStr));
        } catch (ParseException e) {
            LOGGER.error("invalid reference date str: {}, ex: {}", referenceDateStr, e);
        }
        underTest = new StackUsageGenerator();
        MockitoAnnotations.initMocks(this);
        iSUG = new IntervalStackUsageGenerator();
        underTest.setIntervalUsageGenerator(iSUG);
    }

//    @Test
//    public void testGenerateShouldCreateExactUsageForBillingPeriodThatIsLongerThanADay() throws Exception {
//        //GIVEN
//        //set the start date time to 23:10
//        referenceCalendar.roll(HOUR_OF_DAY, false);
//        referenceCalendar.set(MINUTE, 10);
//        Date startDate = referenceCalendar.getTime();
//
//        //date to oct 02 01:50
//        referenceCalendar.set(Calendar.DAY_OF_MONTH, 4);
//        referenceCalendar.set(Calendar.MONTH, Calendar.OCTOBER);
//        referenceCalendar.set(HOUR_OF_DAY, 1);
//        referenceCalendar.set(MINUTE, 50);
//        Date stopDate = referenceCalendar.getTime();
//        LOGGER.info("Start date: {}", startDate);
//        LOGGER.info("Stop date: {}", stopDate);
//
//        CloudbreakEvent startEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent stopEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), stopDate);
//        given(usageRepository.count()).willReturn(0L);
//
//        //WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(Arrays.asList(startEvent, stopEvent));
//
//        //THEN
//        sortByUsagesDate(usageList);
//        Assert.assertTrue("The number of the generated usages is not the expected", usageList.size() == 11);
//        Assert.assertEquals("The start day is wrong", DATE_FORMAT.format(startDate), DATE_FORMAT.format(usageList.get(0).getDay()));
//        Assert.assertEquals("The stop day is wrong", DATE_FORMAT.format(stopDate), DATE_FORMAT.format(usageList.get(usageList.size() - 1).getDay()));
//        Assert.assertEquals("The calculated hours of the last day is invalid", Long.valueOf(24), usageList.get(4).getInstanceHours());
//        Assert.assertEquals("The calculated hours of the last day is invalid", Long.valueOf(2), usageList.get(usageList.size() - 1).getInstanceHours());
//        Assert.assertEquals("The calculated hours of the first day is invalid", Long.valueOf(1), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateExactUsageForBillingPeriodThatIsLongerThanADayWithRounding() throws Exception {
//        //GIVEN
//        //set the start date time to 23:59
//        referenceCalendar.roll(HOUR_OF_DAY, false);
//        referenceCalendar.set(MINUTE, 59);
//        Date startDate = referenceCalendar.getTime();
//
//        //date to oct 02 00:35
//        referenceCalendar.set(Calendar.DAY_OF_MONTH, 4);
//        referenceCalendar.set(Calendar.MONTH, Calendar.OCTOBER);
//        referenceCalendar.set(HOUR_OF_DAY, 0);
//        referenceCalendar.set(MINUTE, 35);
//        Date stopDate = referenceCalendar.getTime();
//        LOGGER.info("Start date: {}", startDate);
//        LOGGER.info("Stop date: {}", stopDate);
//
//        CloudbreakEvent startEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent stopEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), stopDate);
//        given(usageRepository.count()).willReturn(0L);
//
//        //WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(Arrays.asList(startEvent, stopEvent));
//
//        //THEN
//        sortByUsagesDate(usageList);
//        Assert.assertTrue("The number of the generated usages is not the expected", usageList.size() == 10);
//        Assert.assertEquals("The start day is wrong", DATE_FORMAT.format(startDate), DATE_FORMAT.format(usageList.get(0).getDay()));
//        Assert.assertEquals("The stop day is wrong",
//                DATE_FORMAT.format(DATE_FORMAT.parse("2014-10-03")), DATE_FORMAT.format(usageList.get(usageList.size() - 1).getDay()));
//        Assert.assertEquals("The calculated hours of the last day is invalid", Long.valueOf(24), usageList.get(4).getInstanceHours());
//        Assert.assertEquals("The calculated hours of the last day is invalid", Long.valueOf(24), usageList.get(usageList.size() - 1).getInstanceHours());
//        Assert.assertEquals("The calculated hours of the first day is invalid", Long.valueOf(1), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateExactUsageForBillingPeriodThatIsLongerThanADayWithExactHours() throws Exception {
//        //GIVEN
//        //set the start date time to 23:00
//        referenceCalendar.roll(HOUR_OF_DAY, false);
//        Date startDate = referenceCalendar.getTime();
//
//        //date to oct 02 02:00
//        referenceCalendar.set(Calendar.DAY_OF_MONTH, 4);
//        referenceCalendar.set(Calendar.MONTH, Calendar.OCTOBER);
//        referenceCalendar.set(HOUR_OF_DAY, 2);
//        Date stopDate = referenceCalendar.getTime();
//        LOGGER.info("Start date: {}", startDate);
//        LOGGER.info("Stop date: {}", stopDate);
//
//        CloudbreakEvent startEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent stopEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), stopDate);
//        given(usageRepository.count()).willReturn(0L);
//
//        //WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(Arrays.asList(startEvent, stopEvent));
//
//        //THEN
//        sortByUsagesDate(usageList);
//        Assert.assertTrue("The number of the generated usages is not the expected", usageList.size() == 11);
//        Assert.assertEquals("The start day is wrong", DATE_FORMAT.format(startDate), DATE_FORMAT.format(usageList.get(0).getDay()));
//        Assert.assertEquals("The stop day is wrong", DATE_FORMAT.format(stopDate), DATE_FORMAT.format(usageList.get(usageList.size() - 1).getDay()));
//        Assert.assertEquals("The calculated hours of the last day is invalid", Long.valueOf(24), usageList.get(4).getInstanceHours());
//        Assert.assertEquals("The calculated hours of the last day is invalid", Long.valueOf(2), usageList.get(usageList.size() - 1).getInstanceHours());
//        Assert.assertEquals("The calculated hours of the first day is invalid", Long.valueOf(1), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateExactUsageForBillingPeriodThatIsShorterThanAnHour() throws Exception {
//        //GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date startDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date stopDate = referenceCalendar.getTime();
//
//
//        CloudbreakEvent startEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent stopEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), stopDate);
//        given(usageRepository.count()).willReturn(0L);
//
//        //WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(Arrays.asList(startEvent, stopEvent));
//
//        //THEN
//        Assert.assertTrue("The number of the generated usages is not the expected", usageList.size() == 1);
//        Assert.assertEquals("The start day is wrong", Long.valueOf(1), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateExactUsageForBillingPeriodThatIsShorterThanADay() throws Exception {
//        //GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 14);
//        Date startDate = referenceCalendar.getTime();
//
//        referenceCalendar.add(DATE, 1);
//        referenceCalendar.set(HOUR_OF_DAY, 0);
//        referenceCalendar.set(MINUTE, 0);
//        referenceCalendar.set(SECOND, 0);
//        referenceCalendar.set(MILLISECOND, 0);
//        Date stopDate = referenceCalendar.getTime();
//
//
//        CloudbreakEvent startEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent stopEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), stopDate);
//        given(usageRepository.count()).willReturn(0L);
//
//        //WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(Arrays.asList(startEvent, stopEvent));
//
//        //THEN
//        Assert.assertTrue("The number of the generated usages is not the expected", usageList.size() == 1);
//        Assert.assertEquals("The start day is wrong", Long.valueOf(10), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateTwoUsageForBillingPeriodThatIsShorterThanADayAndBillingEventsOnDifferentDays() throws Exception {
//        //GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 14);
//        referenceCalendar.set(MINUTE, 30);
//        Date startDate = referenceCalendar.getTime();
//
//        referenceCalendar.add(DATE, 1);
//        referenceCalendar.set(HOUR_OF_DAY, 6);
//        referenceCalendar.set(MINUTE, 10);
//        Date stopDate = referenceCalendar.getTime();
//
//
//        CloudbreakEvent startEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent stopEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), stopDate);
//        given(usageRepository.count()).willReturn(0L);
//
//        //WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(Arrays.asList(startEvent, stopEvent));
//
//        //THEN
//        Assert.assertEquals("The number of the generated usages is not the expected", 2, usageList.size());
//        Assert.assertEquals("The start day is wrong", Long.valueOf(10), usageList.get(0).getInstanceHours());
//        Assert.assertEquals("The start day is wrong", Long.valueOf(6), usageList.get(1).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateExactUsageForBillingPeriodThatIsShorterThanADayAndRoundingNecessary() throws Exception {
//        //GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.set(MINUTE, 40);
//        Date roundUpStart = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 3);
//        referenceCalendar.set(MINUTE, 45);
//        Date roundUpStop = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.set(MINUTE, 40);
//        Date roundDownStart = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 3);
//        referenceCalendar.set(MINUTE, 20);
//        Date roundDownStop = referenceCalendar.getTime();
//
//        CloudbreakEvent startEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), roundUpStart);
//        CloudbreakEvent stopEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), roundUpStop);
//        CloudbreakEvent startRoundDownEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), roundDownStart);
//        CloudbreakEvent stopRoundDownEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), roundDownStop);
//        given(usageRepository.count()).willReturn(0L);
//
//        //WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(Arrays.asList(startEvent, stopEvent, startRoundDownEvent, stopRoundDownEvent));
//
//        //THEN
//        Assert.assertTrue("The number of the generated usages is not the expected", usageList.size() == 1);
//        Assert.assertEquals("The start day is wrong", Long.valueOf(7), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateExactUsageForBillingPeriodWhenMultipleEventsExistOnTheSameDay() {
//        // GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date startDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date updateDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date availableDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date stopDate = referenceCalendar.getTime();
//
//        CloudbreakEvent startEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent updateEvent = ServiceTestUtils.createEvent(1L, 1, "UPDATE_IN_PROGRESS", updateDate);
//        CloudbreakEvent availableEvent = ServiceTestUtils.createEvent(1L, 1, "AVAILABLE", availableDate);
//        CloudbreakEvent secondStartEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), availableDate);
//        CloudbreakEvent stopEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), stopDate);
//        given(usageRepository.count()).willReturn(0L);
//        List<CloudbreakEvent> events = Arrays.asList(startEvent, availableEvent, updateEvent, secondStartEvent, stopEvent);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        Assert.assertTrue("The number of the generated usages is not the expected", usageList.size() == 1);
//        Assert.assertEquals("The start day is wrong", Long.valueOf(3), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageForMultipleBillingPeriodInOneItem() throws Exception {
//        // GIVEN
//        //2014-09-24 1:20
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.roll(MINUTE, 20);
//        Date startDate = referenceCalendar.getTime();
//
//        //2014-09-26 1:20
//        referenceCalendar.roll(DATE, 2);
//        Date stopDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date restartDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.set(MINUTE, 10);
//        Date terminateDate = referenceCalendar.getTime();
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent stoppedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), stopDate);
//        CloudbreakEvent restartedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), restartDate);
//        CloudbreakEvent terminatedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), terminateDate);
//        given(usageRepository.count()).willReturn(0L);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent, stoppedEvent, restartedEvent, terminatedEvent);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        sortByUsagesDate(usageList);
//        Assert.assertEquals("The number of the generated usages is not the expected", 3, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(23), usageList.get(0).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(24), usageList.get(1).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(2), usageList.get(2).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageForMultipleBillingPeriodOnTheSameDay() throws Exception {
//        // GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.roll(MINUTE, 20);
//        Date startDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date stopDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date restartDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.set(MINUTE, 10);
//        Date terminateDate = referenceCalendar.getTime();
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent stoppedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), stopDate);
//        CloudbreakEvent restartedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), restartDate);
//        CloudbreakEvent terminatedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STOPPED.name(), terminateDate);
//        given(usageRepository.count()).willReturn(0L);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent, stoppedEvent, restartedEvent, terminatedEvent);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        Assert.assertEquals("The number of the generated usages is not the expected", 1, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(2), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageForStackThatIsStillRunningAndUsageTableIsNotEmpty() throws Exception {
//        // GIVEN
//        Calendar start = Calendar.getInstance();
//        start.add(DATE, -1);
//        start.set(HOUR_OF_DAY, 10);
//        Date startDate = start.getTime();
//
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent);
//        given(usageRepository.count()).willReturn(1L);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        verify(eventRepository).save(any(CloudbreakEvent.class));
//        Assert.assertEquals("The number of the generated usages is not the expected", 1, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(14), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageForStackThatIsStillRunningAndUsageTableIsEmptyWithExactStartHour() throws Exception {
//        // GIVEN
//        Calendar start = Calendar.getInstance();
//        start.add(DATE, -3);
//        start.set(HOUR_OF_DAY, 10);
//        start.set(MINUTE, 0);
//        start.set(SECOND, 0);
//        start.set(MILLISECOND, 0);
//        Date startDate = start.getTime();
//
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent);
//        given(usageRepository.count()).willReturn(0L);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        verify(eventRepository).save(any(CloudbreakEvent.class));
//        sortByUsagesDate(usageList);
//        Assert.assertEquals("The number of the generated usages is not the expected", 3, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(14), usageList.get(0).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(24), usageList.get(1).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(24), usageList.get(2).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageForStackThatIsStillRunningAndUsageTableIsEmpty() throws Exception {
//        // GIVEN
//        Calendar start = Calendar.getInstance();
//        start.add(DATE, -3);
//        start.set(HOUR_OF_DAY, 10);
//        start.set(MINUTE, 11);
//        start.set(SECOND, 21);
//        start.set(MILLISECOND, 22);
//        Date startDate = start.getTime();
//
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent);
//        given(usageRepository.count()).willReturn(0L);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        verify(eventRepository).save(any(CloudbreakEvent.class));
//        sortByUsagesDate(usageList);
//        Assert.assertEquals("The number of the generated usages is not the expected", 3, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(14), usageList.get(0).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(24), usageList.get(1).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(24), usageList.get(2).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageForStackThatIsStillRunningAndUsageTableIsEmptyAndExactHour() throws Exception {
//        // GIVEN
//        Calendar start = Calendar.getInstance();
//        start.add(DATE, -3);
//        start.set(HOUR_OF_DAY, 10);
//        start.set(MINUTE, 0);
//        start.set(SECOND, 0);
//        start.set(MILLISECOND, 0);
//        Date startDate = start.getTime();
//
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent);
//        given(usageRepository.count()).willReturn(0L);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        verify(eventRepository).save(any(CloudbreakEvent.class));
//        sortByUsagesDate(usageList);
//        Assert.assertEquals("The number of the generated usages is not the expected", 3, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(14), usageList.get(0).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(24), usageList.get(1).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(24), usageList.get(2).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageWhenUpscaleOccursOnOneDay() throws Exception {
//        // GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.roll(MINUTE, 20);
//        Date startDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date billingChangedDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.set(MINUTE, 10);
//        Date terminateDate = referenceCalendar.getTime();
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent billingChangedEvent = ServiceTestUtils.createEvent(1L, 3, BillingStatus.BILLING_CHANGED.name(), billingChangedDate);
//        CloudbreakEvent terminatedEvent = ServiceTestUtils.createEvent(1L, 3, BillingStatus.BILLING_STOPPED.name(), terminateDate);
//        given(usageRepository.count()).willReturn(0L);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent, billingChangedEvent, terminatedEvent);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        Assert.assertEquals("The number of the generated usages is not the expected", 1, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(4), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageWhenDownscaleOccursOnOneDay() throws Exception {
//        // GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.roll(MINUTE, 20);
//        Date startDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date billingChangedDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.set(MINUTE, 10);
//        Date terminateDate = referenceCalendar.getTime();
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 5, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent billingChangedEvent = ServiceTestUtils.createEvent(1L, 3, BillingStatus.BILLING_CHANGED.name(), billingChangedDate);
//        CloudbreakEvent terminatedEvent = ServiceTestUtils.createEvent(1L, 3, BillingStatus.BILLING_STOPPED.name(), terminateDate);
//        given(usageRepository.count()).willReturn(0L);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent, billingChangedEvent, terminatedEvent);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        Assert.assertEquals("The number of the generated usages is not the expected", 1, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(8), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageWhenMultipleScaleEventOccurOnOneDay() throws Exception {
//        // GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.roll(MINUTE, 20);
//        Date startDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date upScaleDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 2);
//        Date downScaleDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 2);
//        Date reUpScaleDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.set(MINUTE, 10);
//        Date terminateDate = referenceCalendar.getTime();
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent upScaleEvent = ServiceTestUtils.createEvent(1L, 5, BillingStatus.BILLING_CHANGED.name(), upScaleDate);
//        CloudbreakEvent downScaleEvent = ServiceTestUtils.createEvent(1L, 2, BillingStatus.BILLING_CHANGED.name(), downScaleDate);
//        CloudbreakEvent reUpScaleEvent = ServiceTestUtils.createEvent(1L, 6, BillingStatus.BILLING_CHANGED.name(), reUpScaleDate);
//        CloudbreakEvent terminatedEvent = ServiceTestUtils.createEvent(1L, 6, BillingStatus.BILLING_STOPPED.name(), terminateDate);
//        given(usageRepository.count()).willReturn(0L);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent, upScaleEvent, downScaleEvent, reUpScaleEvent, terminatedEvent);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        Assert.assertEquals("The number of the generated usages is not the expected", 1, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(21), usageList.get(0).getInstanceHours());
//    }
//
//    @Test
//    public void testGenerateShouldCreateUsageWhenMultipleScaleEventOccurOnDifferentDays() throws Exception {
//        // GIVEN
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.roll(MINUTE, 50);
//        Date startDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        Date upScaleDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(DATE, 1);
//        Date downScaleDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(DATE, 1);
//        Date reUpScaleDate = referenceCalendar.getTime();
//
//        referenceCalendar.roll(HOUR_OF_DAY, 1);
//        referenceCalendar.set(MINUTE, 10);
//        Date terminateDate = referenceCalendar.getTime();
//
//        CloudbreakEvent startedEvent = ServiceTestUtils.createEvent(1L, 1, BillingStatus.BILLING_STARTED.name(), startDate);
//        CloudbreakEvent upScaleEvent = ServiceTestUtils.createEvent(1L, 5, BillingStatus.BILLING_CHANGED.name(), upScaleDate);
//        CloudbreakEvent downScaleEvent = ServiceTestUtils.createEvent(1L, 2, BillingStatus.BILLING_CHANGED.name(), downScaleDate);
//        CloudbreakEvent reUpScaleEvent = ServiceTestUtils.createEvent(1L, 6, BillingStatus.BILLING_CHANGED.name(), reUpScaleDate);
//        CloudbreakEvent terminatedEvent = ServiceTestUtils.createEvent(1L, 6, BillingStatus.BILLING_STOPPED.name(), terminateDate);
//        given(usageRepository.count()).willReturn(0L);
//        List<CloudbreakEvent> events = Arrays.asList(startedEvent, upScaleEvent, downScaleEvent, reUpScaleEvent, terminatedEvent);
//
//        // WHEN
//        List<CloudbreakUsage> usageList = underTest.generate(events);
//
//        // THEN
//        sortByUsagesDate(usageList);
//        Assert.assertEquals("The number of the generated usages is not the expected", 3, usageList.size());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(111), usageList.get(0).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(54), usageList.get(1).getInstanceHours());
//        Assert.assertEquals("The number of the running hours is not the expected", Long.valueOf(10), usageList.get(2).getInstanceHours());
//    }
//
//    private void sortByUsagesDate(List<CloudbreakUsage> usageList) {
//        Collections.sort(usageList, new Comparator<CloudbreakUsage>() {
//            @Override
//            public int compare(CloudbreakUsage o1, CloudbreakUsage o2) {
//                return o1.getDay().compareTo(o2.getDay());
//            }
//        });
//    }
}